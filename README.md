# SimplyJServer
Open source Java-Web Server made for Java developers to make their Java Web Apps in Pure Java

## It's an alternative for
Servlet, JSP, XAMPP, PHP, JS, NodeJS, Apache, Nginx, and others

## Build and Run
`cd src/ && javac main.java && java main`

## How to use?
Navigate to DOC.md

## Features
- Edit Headers
- Edit packets from Low-Level
- Byte-by-Byte Sending for +250kb files
- Enhanced performance updates
- Comfortable with Windows and Linux
- Plain Java in Static pages
- GZIP all the time
- Against Buffer Overflow, Denial of Service, etc..
- Simple Structure with A clear Documentation
- Convert your Java Application to Java Web Application in less than 2hrs.
- No limit for GET requests parameters

<br><br><br>
**In case of DoS attacks, use `cpulimit -l 200 -- java main` to limit the CPU usage to 2 cores only "depending on how many cores, multiply the allowed-to-use cores * 100 and replace 200". With no `cpulimit` setting everything is fine, but java will use ALL cores, use wisely.**
