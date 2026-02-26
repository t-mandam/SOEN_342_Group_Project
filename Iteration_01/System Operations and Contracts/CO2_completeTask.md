**System Operation** : *completeTask*  
**Contract CO2** : completeTask  

**Operation** : `completeTask(id: integer)`  
**Cross References** : Use Case: Complete Task  
**Preconditions** : task ID exists and task is open  
**Postconditions** :  
+ `Task.status` attribute modified to "completed"
+ `Update` instance created
+ `Update` associated with `UpdateLog`
