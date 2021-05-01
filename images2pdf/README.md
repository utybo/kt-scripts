# images2pdf script

This script creates a PDF file from all of the image files you provide it as input.

## Usage

```
$ kotlin images2pdf.main.kts --help
usage: [-h] -o OUTPUT [-t TITLE] [-a AUTHOR] [-s] [-n] FILES...

required arguments:
  -o OUTPUT,        Output file name
  --output OUTPUT


optional arguments:
  -h, --help        show this help message and exit

  -t TITLE,         Title for the generated PDF file's metadata
  --title TITLE

  -a AUTHOR,        Author for the generated PDF file's metadata
  --author AUTHOR

  -s, --sort        Enable sorting the input file names using Alphanum Sort if
                    set (disabled by default)

  -n,               Disables compressing all of the images in the PDF. This
  --no-compress     can dramatically increase PDF sizes, use with caution!


positional arguments:
  FILES             Files to merge into the output PDF file
```
