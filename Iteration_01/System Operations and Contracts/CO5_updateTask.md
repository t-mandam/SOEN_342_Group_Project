**System Operation** : *updateTask(id, changes)*  
**Contract CO5** : updateTask  

**Operation** : `updateTask(id: integer, changes: Update)`  
**Cross References** : Use Case: Update Task  
**Preconditions** : task.id exists and the respective Update instance has been created   
**Postconditions** :  
+ `Task.attribute` is modified
+ Subtype of `Update` instance is created
+ `Update` is associated to `UpdateLog`
