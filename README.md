# Medical Conferences
## Configurations and Prerequiests:
None

##Initializing the database
The database is initialzied automatically when created with users (2 admins and 5 doctors)

## Assumptions made:
* Admin can only view and administer the conferences he created 
* Admin cannot remove an invitation sent to doctor
* Doctor cannot remove a suggestion he made
* Admin cannot remove a suggestion made by a doctor
* Neither doctors or admins can see ended or canceled conferences (except for calendar view for the ended ones only)
* Doctor can accept a rejected invitation and vice versa
* The application doesn’t provide register new users functionality, rather at creating the database first time, predefined users (admins and doctors) gets inserted into the database and used later (for the info about the users created see class DBInitialData

##Missing functionality and buggy functionality:
* Doctors don’t get a notification when an event is canceled, it just gets removed from their upcoming conferences list and calendar
* If multiple conferences exist at the same day, the calendar view can direct to only one


## Open source dependencies
The libraries dependencies are resolved automatically using gradle, no manual setup needed
* [RxJava ](https://github.com/ReactiveX/RxJava)
* [RxAndroid](https://github.com/ReactiveX/RxAndroid)
* [Mockito](https://github.com/mockito/mockito)
* [Assert J](https://github.com/joel-costigliola/assertj-core)

##Issues faced
A weird issue with jack compiler occured with lambdas, the runtime couldn't find the objects that the lambdas should have caused to be created. It's the first time this happen with me. The quick fix for it is using retro lambda instead and it just works fine.