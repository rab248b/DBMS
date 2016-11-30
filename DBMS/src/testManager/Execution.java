package testManager;
import java.util.ArrayList;
import storageManager.*;


public class Execution {
		
	public Execution(){
		
	}

  public String executeCreateTable(String relationName, ArrayList<String> field_names, ArrayList<FieldType> field_types){
    //=======================Initialization=========================
	  String result =null;
    try {
		System.out.print("=======================Initialization=========================" + "\n");

		// Initialize the memory, disk and the schema manager
		MainMemory mem=new MainMemory();
		Disk disk=new Disk();
		System.out.print("The memory contains " + mem.getMemorySize() + " blocks" + "\n");
		System.out.print(mem + "\n" + "\n");
		SchemaManager schema_manager=new SchemaManager(mem,disk);
   // ExecuteStatements run = new ExecuteStatements();
		disk.resetDiskIOs();
		disk.resetDiskTimer();

		// Another way to time
		long start = System.currentTimeMillis(); 

		//=======================Schema=========================
		System.out.print("=======================Schema=========================" + "\n");

		// Create a schema
		System.out.print("Creating a schema" + "\n");
		//String relationName="firstTable";
		//ArrayList<String> field_names=new ArrayList<String>();
		//ArrayList<FieldType> field_types=new ArrayList<FieldType>();
//    field_names.add("f1");
//    field_names.add("f2");
//    field_names.add("f3");
//    field_names.add("f4");
//    field_types.add(FieldType.STR20);
//    field_types.add(FieldType.STR20);
//    field_types.add(FieldType.INT);
//    field_types.add(FieldType.STR20);
		
		
		Schema schema= new Schema(field_names,field_types);
		Relation relation_reference =schema_manager.createRelation(relationName,schema);
		System.out.println(relation_reference);
   

		// Print the information about the schema
		System.out.print(schema + "\n");
		System.out.print("The schema has " + schema.getNumOfFields() + " fields" + "\n");
		System.out.print("The schema allows " + schema.getTuplesPerBlock() + " tuples per bslock" + "\n");
		System.out.print("The schema has field names: " + "\n");
		field_names=schema.getFieldNames();
		System.out.print(field_names.toString()+"\n");
		System.out.print("The schema has field types: " + "\n");
		field_types=schema.getFieldTypes();
		System.out.print(field_types.toString()+"\n");
		result = "Relation "+ relationName+ " created successfully.";
//    System.out.print("\n");
//    System.out.print("The first field is of name " + schema.getFieldName(0) + "\n");
//    System.out.print("The second field is of type " + (schema.getFieldType(1)) + "\n");
//    System.out.print("The field f3 is of type " + (schema.getFieldType("f3")) + "\n");
//    System.out.print("The field f4 is at offset " + schema.getFieldOffset("f4") + "\n" + "\n");
	} catch (Exception e) {
		// TODO Auto-generated catch block
		result = "Error while creating relation";
		e.printStackTrace();
	}
    return result;

  }
}