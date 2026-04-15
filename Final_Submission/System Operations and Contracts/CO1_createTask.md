**System Operation** : *createTask(title, description)*  
**Contract CO1** : createTask  

**Operation** : `createTask(title: String, description: String)`  
**Cross References** : Use Case: Create Task  
**Preconditions** : title and description are valid Strings  
**Postconditions** :  
+ `Task` instance created
+ `Task.title`, `Task.description`, and `Task.status` respectively set to title, description, and "open"
+ `Task` associated to `TaskCatalog`
