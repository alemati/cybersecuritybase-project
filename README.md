# cybersecuritybase-project  
LINK: https://github.com/alemati/cybersecuritybase-project  
Installation:
* down repository using commad 'git clone https://github.com/alemati/cybersecuritybase-project’  
* open project in your IDE and start the program  
* program will run in http://localhost:8080/   

Program gives user an opportunity to create an account and keep track of his/her bank accounts. Program has admin user. Admin can see list of users and delete them.  

Flaw 1: Broken Authentication  

Program has admin user. Admin can open admin page by using default credentials (username:admin and password:1234). Using such easy way to obtain admin rights is a huge security vulnerability. Attacker can enter program as admin by assuming that admin uses ‘admin’ as username and then trying to find right password. One way to do it is to brute force list of known common passwords (for instance: using OWASP Zap or Burp Suite).

This vulnerability can be fixed in of variety different ways. First of all, admin should be able to change password (and username for good measure). New password should be complex and unique in order to be immune to simple brute force attacks. In addition, multiple layers of authentication can be used (for instance mobile phone certification)

Flaw 2: Broken access control

Program doesn’t use any sort of authentication checking and all pages that program provides can be reached by simply entering right URL. Because application doesn’t check users rights to see page (due to lack of authentication) all pages can be opened by an unauthenticated attacker. If attacker happens to know a username of targeted user, attacker might try to enter username as path (for instance if ‘elsa’ is used username: http://localhost:8080/elsa) and open targets page. After trying out different URL paths attacker may find out that simple path /admin opens admin page and reveals sensitive data. 

Several actions must be taken in order to eliminate (reduce) this vulnerability. First of all, authentication feature should be added with at least three different values: admin (to grant admin rights),user (for programs users) and guest (for unregistered users). URLs should be encrypted. Also data that is used in GET and POST methods and is visible in source code should be encrypted.

Flaw 3: Injection

For storing data program uses JPA and local SQL database. When new account is created it is stored to local database and if application is restarted all users will be loaded once again. Users bank information on the other is store only in JPA and will disappear if program is restarted. Malicious user however can delete user account data from local database using injection. It can be done by creating new account (in path /registration) with random name and particular password. Password should be  __'); DROP TABLE User; --__ 
This is classical injection attack. Now all attacker has to do is just to push “Create new account” -button. While program is running account information will still be available (because JPA still has it), but after restart where will be not any account information.

Fix for this problem is fairly simple. Injection is possible because SQL query that is responsible for inserting new account information into local database is coded dynamically ( "INSERT INTO User (id, username, password) VALUES ("+ id + ", '" + un+ "', '" + pw + "')" ). Better way is to use prepared statement using safe mechanism. 

For example: 
PreparedStatement preparedStatement = conn.prepareStatement("INSERT INTO User (id, username, password) VALUES (?)");
preparedStatement.setString(1, 1);
preparedStatement.setString(2, "IamNewUser");
preparedStatement.setString(3, "myStrongPassword");
ResultSet result = preStatement.executeQuery();

Flaw 4: XSS vulnerability

Program is prone to XSS attacks. In one segment program doesn’t make sure if user input is correct and doesn’t validate nor escape user input. That gives attacker an opportunity to insert his/her malicious piece of code into source code (in our case into HTML code) that later is triggered in right circumstances. In case of our application while creating new account user can enter his javascript code into ‘name’ field. For instance it can be: <script>alert(document.cookie);</script>. Now, when new account with such username is created, when admin opens admin page, piece of code that lists all usernames to admin will be triggered. Because of the security hole, instead of just printing <script>alert(document.cookie);</script>, inserted javascript code will be executed. Depending on javascript code content attacker can make damage or obtain data. 

First very simple fix is to make sure that all user input text is escaped before printing. In admin.html change ‘th:utext=’ to ‘th:text=’ when printing username. Also it is sensible to check if user inputs are correct before storing it into database.

Flaw 5: Using components with known vulnerabilities

Using OWASP Dependency Check (added dependency-check-maven plugin and run: mvn dependency-check:check) I found multiple vulnerabilities in used dependencies and several of them have crucial or high risk. In order to make the application as safe as possible all of the should be taken care of (deleted or updated to safe versions).

For instance one of reported vulnerabilities was CVE-2018-11039. It enables attacker to escalate previously mentioned possibility of XSS attack to XST (Cross Site Tracing) attack. One of possible solution to this problem is to change springframework release in pom.xml file from 1.4.2 to 1.4.3
