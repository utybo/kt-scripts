@file:DependsOn("org.apache.pdfbox:pdfbox:2.0.23")
@file:DependsOn("net.grey-panther:natural-comparator:1.1")
@file:DependsOn("com.xenomachina:kotlin-argparser:2.0.7")

import com.xenomachina.argparser.ArgParser
import com.xenomachina.argparser.default
import com.xenomachina.argparser.mainBody
import net.greypanther.natsort.SimpleNaturalComparator
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDDocumentInformation
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.graphics.image.JPEGFactory
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject

// A stupid script for converting images to a PDF file. Page sizes will (by design) not be constant.

class Args(parser: ArgParser) {
    val output by parser.storing("-o", "--output", help = "Output file name")

    val title: String? by parser.storing("-t", "--title", help = "Title for the generated PDF file's metadata")
        .default(null)

    val author: String? by parser.storing("-a", "--author", help = "Author for the generated PDF file's metadata")
        .default(null)

    val sort by parser.flagging(
        "-s",
        "--sort",
        help = "Enable sorting the input file names using Alphanum Sort if set (disabled by default)"
    )

    val nocompress by parser.flagging(
        "-n",
        "--no-compress",
        help = "Disables compressing all of the images in the PDF. This can dramatically increase PDF sizes, use with caution!"
    )

    val files by parser.positionalList("Files to merge into the output PDF file", 1..Int.MAX_VALUE)
}

mainBody {
    val parsedArgs = ArgParser(args).parseInto(::Args)

    val images = if (parsedArgs.sort)
        parsedArgs.files.sortedWith(SimpleNaturalComparator.getInstance())
    else
        parsedArgs.files

    PDDocument().use { pdf ->
        processDocumentInformation(pdf.documentInformation, parsedArgs)

        images.forEach {
            println("Processing $it")
            val image = PDImageXObject.createFromFile(it, pdf).let { img ->
                if (parsedArgs.nocompress) img
                else JPEGFactory.createFromImage(pdf, img.opaqueImage)
            }
            val page = PDPage(PDRectangle(image.width.toFloat(), image.height.toFloat()))
            pdf.addPage(page)
            PDPageContentStream(pdf, page, PDPageContentStream.AppendMode.APPEND, true, true).use { stream ->
                stream.drawImage(image, 0f, 0f)
            }
        }
        pdf.save(parsedArgs.output)
    }
}

fun processDocumentInformation(info: PDDocumentInformation, parsedArgs: Args) {
    if (parsedArgs.title != null)
        info.title = parsedArgs.title
    if (parsedArgs.author != null)
        info.author = parsedArgs.author
    info.creator = "images2pdf.main.kts"
}
