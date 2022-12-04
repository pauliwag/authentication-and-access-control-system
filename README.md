# Authentication and Access Control System

***Summary***

My implementation of an authentication and access control system prototype for a financial planning and investment banking company's proprietary financial software and data systems, to better support their clients.  

Includes a PBKDF2 salt-hashing password manager, user enrolment and verification, proactive password checking, and enforcement of an RBAC-ABAC-OBAC hybrid access control policy represented by an access control matrix, allowing users to perform a variety of authorized operations.

---

***Running the app***

The driving classes are `UserEnrolment.java` and `UserLogin.java`, which, as their names suggest, respectively drive enrolment and logging into the system—run them accordingly and follow the ensuing terminal prompts. Similarly, one can run `TestAccessControlPolicy.java` and `TestPasswordManager.java` and observe the testing results in the terminal. Ideally, JUnit would have been leveraged for automated testing, but was not due to time constraints. The password store is prepopulated with a few users.