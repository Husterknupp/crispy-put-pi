Simple approach towards a Put Wall like system (https://www.youtube.com/watch?v=NToQfX3NseQ)

**Using**
 - Raspberry Pi 2
 - Pi4J: http://pi4j.com/example/control.html
 - and this Raspberry Pi tutorial: https://developer-blog.net/hardware/raspberry-pi-gpio-schnittstelle-teil-1/
 - easy barcode generator: http://barcode.tec-it.com/de
 - Spark-Java framework for creating web applications: http://sparkjava.com/documentation.html#sessions
 - Python requests library: http://docs.python-requests.org/en/master/

**Compile**

 ``mvn package``
 
**Run on the Raspberry**

 ``sudo java -jar putpi-1.0-SNAPSHOT.jar``
 
 ``chmod +x hack4.py``
 
 ``./hack4.py``
