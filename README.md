
Jidget
======

Jidget is a JavaFX 2 Desktop Widget System

Status: Prototype

Features
--------

  * Define widgets with FXML or Jidget XML
    * A Jidget is a single file
    * Drop them in a folder and they can be loaded
	* Easy to share 
  * Beans to collect data
    * Load URLs, images
	* Calculations, Format output
	* Measure CPU usage (more will follow)
	* Add your own beans with plug-ins
  * JavaFX nodes to show data
    * JavaFX 2 controls (not all yet, the rest will follow soon)
    * Add your own components with plug-ins
  * CSS to customize the looks
  * Properties to configure personal settings
  
Getting started
---------------

  * Download and extract to a folder of your choice
  * Run `start.bat`
    * If you are on Mac/Linux, you will figure it out ;-)
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
	  * Thus quicker to adapt someone else's Jidget to better suit your needs
	* Easier to write
	  * I don't trust tools for fine-grained to do 