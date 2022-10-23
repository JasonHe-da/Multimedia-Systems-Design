# Multimedia-Systems-Design

HW2 programming part

command example:
javac ImageSeg.java

You may take two folders which are containing 480 rgb image files.
Program will automatically read all those rgb in order
It may take 10 to 15 seconds preprocess all those images in order to keep video 24 fps

java ImageSeg background_subtraction_2 background_moving_2 1

mode 0
foreground video does not have any constant colored green screen and while this is a hard problem to find automatically, the foreground videos we give you will have the foreground element (actor, object) moving in every frame while the camera is static.

mode 1
detect the green screen pixels in the foreground video and replace them with the corresponding background video pixels in all the frames
