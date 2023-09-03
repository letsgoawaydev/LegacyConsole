package com.letsgoaway.legacyconsole;

public class TextAnims {
    public static String[] chestRefill = {
	    "\uE090",
	    "\uE091",
	    "\uE092",
	    "\uE093",
	    "\uE094",
	    "\uE095",
	    "\uE096",
	    "\uE097",
	    "\uE098",
	    "\uE099",
	    "\uE09A",
	    "\uE09B",
	    "\uE09C",
	    "\uE09D",
	    "\uE09E",
	    "\uE09F",
	    "\uE0A2",
	    "\uE0A3",
	    "\uE0A4",
	    "\uE0A5",
	    "\uE0A6"
    };

    public static String getFrame(String[] anim, int frame) {
	return anim[frame % anim.length];
    }
}
