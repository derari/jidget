
Jidget
======

Jidget is a JavaFX 2 Desktop Widget System

Status: Prototype

[Download](http://turtlecat.de/jidget/jidget-snapshot.zip)

Features
--------

  * Define JavaFX widgets (Jidgets) with FXML or Jidget XML
    * A Jidget is a single file
    * Drop them in a folder and they can be loaded
	* Easy to share 
  * Beans to collect data
    * Load URLs, images
	* Calculations, Format output
	* Measure CPU usage (more will follow)
	* Add your own beans via plug-ins
  * JavaFX nodes to show data
    * JavaFX 2 controls (not all yet, the rest will follow soon)
    * Add your own components via plug-ins
  * CSS to customize the looks
  * Properties to configure personal settings
  
Getting started
---------------

  * [Download](http://turtlecat.de/jidget/jidget-snapshot.zip) and extract to a folder of your choice
  * Just run the jar (e.g., `java -jar jidget-app-0.1-SNAPSHOT.jar`)
    * Make sure you have the latest jdk (1.7_13)
  * Activate some Jidgets and drag them over the screen
  * I said it's just a prototype
  
Getting involved
----------------

  * Like the idea?
    * Tell us!
	* Make some better looking Jidgets!
	  * Should not be hard.
  * Need some feature/found a bug?
    * Tell us!
	* Or do it yourself :-)
	
Why Jidget XML?
---------------

  * FXML is quite OK
    * Tool support
	* Established (more or less) standard
    * But
      * Wasn't even a thing when I started this project
	  * Is very verbose
  * Jidget XML
    * Easy to read
	  * Thus easier to adapt someone else's Jidget to better suit your needs
	* Easier to write
	  * I don't trust tools for fine-grained layouting