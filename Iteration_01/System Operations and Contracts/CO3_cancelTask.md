**System Operation** : *cancelTask(id)*  
**Contract CO3** : cancelTask  

**Operation** : `cancelTask(id: integer)`  
**Cross References** : Use Case: Cancel Task  
**Preconditions** : task id exists and task is open  
**Postconditions** :  
+ `Task.status` is modified to "cancelled"
+ `Update` instance is created
+ `Update` instance is associated to `TaskCatalog`
