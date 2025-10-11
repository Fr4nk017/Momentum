

Java 21 upgrade
----------------

This project has been updated to target Java 21 (LTS). To build locally you must install a JDK 21 on your machine and point Gradle/IDE to it.

Windows quick steps:

1. Install a JDK 21 distribution (Adoptium / Azul / Oracle). Example installer puts it in `C:\Program Files\Java\jdk-21`.
2. Set JAVA_HOME in an elevated PowerShell (replace path if different):

	$env:JAVA_HOME = 'C:\Program Files\Java\jdk-21'

	To set it permanently, use System Properties or PowerShell's setx command (restart terminal/IDE after).

3. Optionally point Gradle to a specific JDK by editing `gradle.properties` and setting:

	org.gradle.java.home=C:/Program Files/Java/jdk-21

4. From the repository root run the Gradle build (Windows PowerShell):

	./gradlew clean build

If you hit compilation errors after switching the JDK, please paste the build output and I'll help fix any Java 21 related issues.

