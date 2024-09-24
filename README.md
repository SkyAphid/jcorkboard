![logo](https://github.com/user-attachments/assets/b5ec9219-dbb1-4d2b-92c5-4116757ce8aa)

**JCorkboard** is a lightweight Java-based JSON Importer for [Corkboard-based projects!](https://github.com/SkyAphid/corkboard)

When you run your Corkboard-made JSON file through its importer, it'll convert the project into usable objects for your program. Alternatively, if you're using a different language, it should be rather easy to convert our [CorkboardJSONImporter](https://github.com/SkyAphid/jcorkboard/blob/main/src/main/java/com/nokoriware/corkboard/CorkboardJSONImporter.java) class into any other language.

Try out our test program, [JCorkboardHelloWorldExample](https://github.com/SkyAphid/jcorkboard/blob/main/src/main/test/com/nokoriware/corkboard/test/JCorkboardHelloWorldExample.java), and select the hello-world.json file for a demonstration of this library. Loading a Corkboard project is as easy as this:

`CorkboardProject project = CorkboardJSONImporter.read(f);`

From that project object, you'll have access to all nodes and connections in the project, along with handle tools that will let you navigate them in your own software.

Happy programming!

[Click here to check out Corkboard's separate project page.](https://github.com/SkyAphid/corkboard)
