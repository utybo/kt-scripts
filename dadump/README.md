# dadump script

Script for downloading a series of images from any service where artists use the convention of adding links labeled with `NEXT` to direct to the next page.

All of the variables at the top of the script can be modified. They are currently valid for downloading stuff from DeviantArt. **Do not use this to scrape websites!** This is mostly intended for enjoying comics in an offline fashion.

The downloaded images are all dumped into a `downloaded` folder in your current directory.

This script takes one mandatory argument, which is the first URL to visit.

```
$ kotlin dadump.main.kts https://example.com/post/abcdefgh
```
