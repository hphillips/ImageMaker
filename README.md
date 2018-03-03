This project is for a tool I use to turn text into an image. I used this in a twitter bot so I could include text longer than 140 characters in its tweets. 


Text is read from standard input by default.


Options:
-f : will read text from a file instead
-t : set text color using the string for the name of the color "-t Black"
-b : like -t but for the background color
-trgp: set text color using 3 integers between 0 and 255 e.g. "-trgp 255 0 0" for red
-brgp: set background color using 3 integers between 0 and 255 e.g. "-trgp 255 0 0" for red
-rm: set read mode. One option "-rm lines" which treats each line in the input as a new piece of text that should be converted to an image. Default behavior is to ingest all the text and create a single image.
-om: set render mode. One option "-om literal" which uses new-lines in the text to determine when to create a new line. Default behavior is to determine when to insert a new line based on what produces a "nice" image based on a maximum and minium line widths
-c: centers the text in the image 
