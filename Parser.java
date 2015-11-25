/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package parser;
import java.io.BufferedReader;  
import java.io.FileInputStream;  
import java.io.FileReader;  
import java.io.IOException;  
import java.io.InputStreamReader; 
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;
/**
 *
 * @author ZSY
 */
public class Parser {
    public static  String[] tmp;
    SchemaManager manager;
    Disk disk;
    MainMemory mem;
    public Parser(){
    tmp=new String[100];      
    mem=new MainMemory();
    disk=new Disk();
    manager=new SchemaManager(mem,disk);
    
    }
    public final void readF1(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(  
                new FileInputStream(filePath))); 
        for (String line = br.readLine(); line != null; line = br.readLine()) {  
            tmp=line.split(" ");
            parse();  
        }  
        br.close();  
    
    }

    public void parse(){;
       switch(tmp[0]){
           case "CREATE": 
               create();break;
           case "DELETE":
               delete();break;
           case "INSERT":
               insert();break;
           case "DROP":
               drop();break;
           case "SELECT":
               select();break;
       
       }
    }
    
    public HashMap createmap(){
        HashMap map = new HashMap<String, Integer>();
        map.put("(", 0);
        map.put("[", 0);
        map.put("*", 1);
        map.put("/", 1);
        map.put("+", 2);
        map.put("-", 2);
        map.put(">", 3);
        map.put("=", 3);
        map.put("<", 3);
        map.put("NOT", 4);
        map.put("AND", 5);
        map.put("OR", 6);
        return map;
    }

    public boolean priority(String pre, String post, HashMap map){
	if((int)map.get(pre) > (int)map.get(post))
		return false;   //push into stack
	else
		return true;
}
//calculate and puch value back 
public void apply(Stack<String> values, String op, Tuple t){
    int num1, num2 = 0;
	if(op.equals("NOT")){
		String operater = values.pop();
		if(operater.equals("true"))
			values.push("false");
		else
			values.push("true");
	}
	if(op.equals("AND")){
		String opt1 = values.pop();
		String opt2 = values.pop();
		if(opt2.equals("true") && opt1.equals("true"))
			values.push("true");
		else
			values.push("false");
	}
	if(op.equals("OR")){
		String opt1 = values.pop();
		String opt2 = values.pop();
		if(opt2.equals("false") && opt1.equals("false"))
			values.push("false");
		else
			values.push("true");
	}
	if(op.equals(">")){
		String opt1 = values.pop();
		String opt2 = values.pop();
                
                if(isDigit(opt2)){
                    if(Integer.parseInt(opt2) > Integer.parseInt(opt1))
                        values.push("true");
                    else
                        values.push("false");
                }
                else{
		Field v2 = tuplegetvalue(t, opt2);
		if(isDigit(opt1)){
			int opint = Integer.parseInt(opt1);
			if(v2.integer > opint)
				values.push("true");
			else
				values.push("false");
		}
			
		else{
                    Field v1 = tuplegetvalue(t, opt1);
                    if(v2.integer > v1.integer)
			values.push("true");
                    else
			values.push("false");
                }
                }
	}

	if(op.equals("<")){
		String opt1 = values.pop();
		String opt2 = values.pop();

		if(isDigit(opt2)){
                    if(Integer.parseInt(opt2) < Integer.parseInt(opt1))
                        values.push("true");
                    else
                        values.push("false");
                }
                else{
                Field v2 = tuplegetvalue(t, opt2);
		if(isDigit(opt1)){
			int opint = Integer.parseInt(opt1);
			if(v2.integer < opint)
				values.push("true");
			else
				values.push("false");
		}
			
		else{
                    Field v1 = tuplegetvalue(t, opt1);
                    if(v2.integer < v1.integer)
			values.push("true");
                    else
			values.push("false");
		}
                }
	}

	if(op.equals("=")){
		String opt1 = values.pop();
		String opt2 = values.pop();
                
                if(isDigit(opt2)){
                    if(Integer.parseInt(opt1) == Integer.parseInt(opt2))
                        values.push("true");
                    else
                        values.push("false");
                }
                else{
                    Field v2 = tuplegetvalue(t, opt2);
		if(isDigit(opt1)){
			int opint = Integer.parseInt(opt1);
			if(v2.integer == opint)
				values.push("true");
			else
				values.push("false");
		}
			
		else
			if(opt1.charAt(0) == '\"'){
				String opstr = opt1.replace('\"', ' ').trim();
				if(v2.str.equals(opstr))
					values.push("true");
				else
					values.push("false");
			}
				
			else{
				Field v1 = tuplegetvalue(t, opt1);
				if(v2.integer == v1.integer || v2.str.equals(v1.str))
					values.push("true");
				else
					values.push("false");
			}
                }
	}

	if(op.equals("+")){
        String s1 = values.pop();
        String s2 = values.pop();
        if(isDigit(s1))
            num1 = Integer.parseInt(s1);
        else{
            Field t1 = tuplegetvalue(t, s1);
            num1 = t1.integer;
        }

        if(isDigit(s2))
            num2 = Integer.parseInt(s2);
        else{
            Field t2 = tuplegetvalue(t, s2);
            num2 = t2.integer;
        }
            values.push(Integer.toString(num1 + num2));
	}

	if(op.equals("-")){
		String s1 = values.pop();
        String s2 = values.pop();
        if(isDigit(s1))
            num1 = Integer.parseInt(s1);
        else{
            Field t1 = tuplegetvalue(t, s1);
            num1 = t1.integer;
        }

        if(isDigit(s2))
            num2 = Integer.parseInt(s2);
        else{
            Field t2 = tuplegetvalue(t, s2);
            num2 = t2.integer;
        }
            values.push(Integer.toString(num2 - num1));
	}

	if(op.equals("*")){
            String s1 = values.pop();
        String s2 = values.pop();
        if(isDigit(s1))
            num1 = Integer.parseInt(s1);
        else{
            Field t1 = tuplegetvalue(t, s1);
            num1 = t1.integer;
        }

        if(isDigit(s2))
            num2 = Integer.parseInt(s2);
        else{
            Field t2 = tuplegetvalue(t, s2);
            num2 = t2.integer;
        }
            values.push(Integer.toString(num1 * num2));
	}

	if(op.equals("/")){
		String s1 = values.pop();
        String s2 = values.pop();
        if(isDigit(s1))
            num1 = Integer.parseInt(s1);
        else{
            Field t1 = tuplegetvalue(t, s1);
            num1 = t1.integer;
        }

        if(isDigit(s2))
            num2 = Integer.parseInt(s2);
        else{
            Field t2 = tuplegetvalue(t, s2);
            num2 = t2.integer;
        }
            values.push(Integer.toString(num2 / num1));
	}
}

public Field tuplegetvalue(Tuple t, String opt){
	return t.getField(opt);
}

public boolean isDigit(String num){
	String regex = "[0-9]+";
	return num.matches(regex);
}
//SELECT * FROM course WHERE exam + homework = 200
//where[] = exam + homework = 200
//relation = course
public boolean whereparse(String[] where, Tuple t){
	Stack<String> values = new Stack<String>();
	Stack<String> ops = new Stack<String>();
        HashMap map = createmap();

	for(int i = 0; i < where.length; i++){
		if(where[i].equals("(") || where[i].equals("["))
			ops.push(where[i]);
                else
		if(where[i].equals(")") || where[i].equals("]")){
			while(!ops.empty() && !ops.peek().equals("(")){
				apply(values, ops.pop(), t);
			}
                        if(!ops.empty() && ops.peek().equals("("))
                            ops.pop();
		}
                else
		if(!where[i].equals("(") && !where[i].equals("[") && map.containsKey(where[i])){
			while(!ops.empty() && !ops.peek().equals("(") && !ops.peek().equals("[") && priority(ops.peek(), where[i], map)){
				apply(values, ops.pop(), t);
			}
			ops.push(where[i]);
		}
		else{
			values.push(where[i]);
		}
	}
	while(!ops.empty()){
		apply(values, ops.pop(), t);
	}
	String res = values.pop();
	if(res.equals("true"))
		return true;
	else
		return false;
}
    
    public boolean fieldequal(Field a, Field b){
        if(a.integer == b.integer && a.str.equals(b.str))
            return true;
        else
            return false;
    }
    
    public void create(){
        String Relation_name=tmp[2];
        ArrayList<String> Field_name=new ArrayList<>();
        ArrayList<FieldType> Field_type=new ArrayList<>();
        for(int i=3;i<tmp.length;i++){
            String str=tmp[i];
            str = str.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            if(i%2==1){
            Field_name.add(str);
            }
            else{
            if(str.equals("INT"))
                Field_type.add(FieldType.INT);
            else
                Field_type.add(FieldType.STR20);
            }
        }
        Schema schema=new Schema(Field_name,Field_type);
        Relation relation=manager.createRelation(Relation_name,schema);
    }
    
        public void insert(){
        String Relation_name=tmp[2];
        int posOfValues=0;
        ArrayList<String> Field_name=new ArrayList<>();
        for(int i=3;i<tmp.length;i++){
            if(tmp[i].equals("VALUES")){
                posOfValues=i+1;
                break;
            }
            String str=tmp[i];
            str = str.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            Field_name.add(str);
        }
        ArrayList<Field> fields=new ArrayList<>();
        Field modelField=new Field();
        Schema schema=manager.getSchema(Relation_name);
        for(int i=posOfValues;i<tmp.length;i++){
            String str = tmp[i];
            str = str.replaceAll("(?i)[^a-zA-Z0-9\u4E00-\u9FA5]", "");
            if(schema.getFieldTypes().get(i - posOfValues) == FieldType.STR20){
                modelField.type = FieldType.STR20;
                modelField.str=str;
            }
            else{
                modelField.type=FieldType.INT;
                if(str!="NUll"){
                modelField.integer=Integer.valueOf(str);
                }
            }
            fields.add(new Field(modelField));
        }
        Tuple newTuple=manager.getRelation(Relation_name).createTuple();
        for(int i=0;i<fields.size();i++){
            if(fields.get(i).type==FieldType.STR20){
                newTuple.setField(Field_name.get(i), fields.get(i).str);
            }
            else{
                newTuple.setField(Field_name.get(i),fields.get(i).integer);
            }
        }
        //Need to push the newly created tuple into the relation.
        int memory_block_index=3;
        Block b=mem.getBlock(memory_block_index);
        b.clear();
        b.appendTuple(newTuple);
        manager.getRelation(Relation_name).setBlock(manager.getRelation(Relation_name).getNumOfBlocks(), memory_block_index);
        //System.out.println(manager.getRelation(Relation_name).getNumOfTuples());
        //System.out.println(schema.getTuplesPerBlock());
    }
        
    public void delete(){
        String Relation_name = tmp[2];
        Schema schema = manager.getSchema(Relation_name);
        Relation relation = manager.getRelation(Relation_name);
        
        if(tmp.length > 3){
            String Field_name = tmp[4];
            String value = tmp[6].replace('\"', ' ').trim();
            Field v = new Field();
            if(schema.fieldNameExists(Field_name)){                
                v.type = schema.getFieldType(Field_name);
                if(v.type == FieldType.INT)
                    v.integer = Integer.parseInt(value);
                else
                    v.str = value;
            }
            for(int i=0; i<relation.getNumOfBlocks(); i++){
                relation.getBlock(i, 1);
                Block block = mem.getBlock(1);
                for(int j=0; j<block.getNumTuples(); j++){
                    Tuple tuple = block.getTuple(j);
                    Field temp = tuple.getField(Field_name);
                    if(fieldequal(temp,v)){
                        //System.out.println(temp.str);
                        tuple.invalidate();
                        //System.out.println("Delete successfully!");
                    }
                }
            }
        }
       else{
            relation.deleteBlocks(0);
        }
    } 
    
    public void drop(){
        String Relation_name=tmp[2];
        manager.deleteRelation(Relation_name);
    }
    
    public void select(){
        String str1=tmp[1].replaceAll("(?i)[^a-zA-Z0-9.*\u4E00-\u9FA5]", "");
        int whereposition = Integer.MIN_VALUE;

        if(str1.equals("*")){
            String[] relation_names=new String[6];
            boolean hasWhere=false;
            for(int i=3;i<tmp.length;i++){
                if(tmp[i].equals("WHERE")){
                    hasWhere=true;
                    whereposition = i;
                    break;
                }
                relation_names[i-3]=tmp[i].replace(',',' ').trim();
            }
            if(!hasWhere){
                for(int i=0;i<relation_names.length;i++){
                    String Relation_name=relation_names[i]; 
                    Relation relation=manager.getRelation(Relation_name);
                    for(int j=0; j<relation.getNumOfBlocks(); j++){
                        relation.getBlock(j, 1);
                        Block block = mem.getBlock(1);
                        for(int k=0; k<block.getNumTuples(); k++){
                            Tuple tuple = block.getTuple(k); 
                            System.out.println(tuple.toString());     
                        }
                    }
                }        
            }
            else{
                String[] where = new String[tmp.length - whereposition - 1];
                for(int i = 0; i < where.length; i++){
                    where[i] = tmp[whereposition + i + 1];
                }
                String Relation_name=relation_names[0];
                Relation relation=manager.getRelation(Relation_name);
                for(int j=0; j<relation.getNumOfBlocks(); j++){
                    relation.getBlock(j, 1);
                    Block block = mem.getBlock(1);
                    for(int k=0; k<block.getNumTuples(); k++){
                        Tuple tuple = block.getTuple(k); 
                        if(whereparse(where, tuple)){
                            System.out.println(tuple.toString());   
                        }                         
                    }
                }
            }
                
                        
            
        }
    }
    
        
    public static void main(String[] args) throws IOException{
    Parser parser=new Parser();
    parser.readF1("/Users/jiaohongyang/Documents/csce608/TinySQL_linux.txt");
    
    }
}

