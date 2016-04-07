
package parser;
import java.io.*;  
import java.util.*;


public class Parser {
    public static  String[] tmp;
    SchemaManager manager;
    Disk disk;
    MainMemory mem;
    String lastRelation;
    String command;

    BufferedWriter out;
    public Parser() throws IOException{
     
    out = new BufferedWriter(new FileWriter(new File("/Users/jiaohongyang/Desktop/output.txt")));
    
    command = null;
    tmp=new String[100];      
    mem=new MainMemory();
    disk=new Disk();
    manager=new SchemaManager(mem,disk);
    lastRelation = null;
    
    }
    public final void readF1(String filePath) throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(  
                new FileInputStream(filePath))); 
        for (String line = br.readLine(); line != null; line = br.readLine()) {  
            command = line;
            tmp=line.split(" ");
            parse();  
        }  
        br.close();  
    
    }

    public void parse(){;
       switch(tmp[0]){
           case "CREATE": 
               System.out.println(command);
               write(command,true);
               lastRelation = null;
               create();
               System.out.println("The accumulated disk I/Os for this command is " + disk.getDiskIOs());
               write("The accumulated disk I/Os for this command is " + disk.getDiskIOs(),true);
               System.out.println("The execution time for this command is " + disk.getDiskTimer());
               write("The execution time for this command is " + disk.getDiskTimer(),true);
               disk.resetDiskIOs();
               disk.resetDiskTimer();
               System.out.println();
               write("",true);
               break;
           case "DELETE":
               lastRelation = null;
               System.out.println(command);
               write(command,true);
               delete();
               System.out.println("The accumulated disk I/Os for this command is " + disk.getDiskIOs());
               write("The accumulated disk I/Os for this command is " + disk.getDiskIOs(),true);
               System.out.println("The execution time for this command is " + disk.getDiskTimer());
               write("The execution time for this command is " + disk.getDiskTimer(),true);
               disk.resetDiskIOs();
               disk.resetDiskTimer();
               System.out.println();
               write("",true);
               break;
           case "INSERT":
               System.out.println(command);
               write(command,true);
               insert();
               System.out.println("The accumulated disk I/Os for this command is " + disk.getDiskIOs());
               write("The accumulated disk I/Os for this command is " + disk.getDiskIOs(),true);
               System.out.println("The execution time for this command is " + disk.getDiskTimer());
               write("The execution time for this command is " + disk.getDiskTimer(),true);
               disk.resetDiskIOs();
               disk.resetDiskTimer();
               System.out.println();
               write("",true);
               break;
           case "DROP":
               System.out.println(command);
               write(command, true);
               lastRelation = null;
               drop();
               System.out.println("The accumulated disk I/Os for this command is " + disk.getDiskIOs());
               write("The accumulated disk I/Os for this command is " + disk.getDiskIOs(),true);
               System.out.println("The execution time for this command is " + disk.getDiskTimer());
               write("The execution time for this command is " + disk.getDiskTimer(),true);
               disk.resetDiskIOs();
               disk.resetDiskTimer();
               System.out.println();
               write("",true);
               break;
           case "SELECT":
               System.out.println(command);
               write(command,true);
               lastRelation = null;
               select();
               System.out.println("The accumulated disk I/Os for this command is " + disk.getDiskIOs());
               write("The accumulated disk I/Os for this command is " + disk.getDiskIOs(),true);
               System.out.println("The execution time for this command is " + disk.getDiskTimer());
               write("The execution time for this command is " + disk.getDiskTimer(),true);
               disk.resetDiskIOs();
               disk.resetDiskTimer();
               System.out.println();
               write("",true);
               break;
       
       }
    }
    public void write(String s, boolean n){
        
        try { 
            if(n)
            out.write(s+"\r\n");
            else
                out.write(s + "\t");
            out.flush(); 
        } catch (Exception e) {  
            e.printStackTrace();  
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
                    if(v2.integer == v1.integer || v2.str != null && v2.str.equals(v1.str))
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

    public void select_with_star_no_where(String[] relation_names){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        int i = 0;
        ArrayList<Tuple> res = new ArrayList<Tuple>();;
        for( i = 0; i < relation_names.length; i++){
            if (relation_names[i] == null) {
                break;
            }
        }
        ArrayList<String> rnames = new ArrayList<String>();
        for (int j = 0; j < i; j++) {
          rnames.add(relation_names[j]);
        }
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names[0]).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names[0]).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names[cj + 1]).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names[cj + 1]).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                  tuples2.add(block_for_2.getTuple(tuple_index_2));
                }  
            }
            res = new ArrayList<Tuple>();
            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], relation_names, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0){
                manager.deleteRelation(cartesian_join[cj - 1]);
            }
            ter = cj;
        }
        
        Schema schema = manager.getSchema(cartesian_join[ter]);
        for(int findex = 0; findex < schema.getNumOfFields(); findex++){
            System.out.print(schema.getFieldName(findex)+" ");
            write(schema.getFieldName(findex),false);
        }
        System.out.println();
        write("", true);
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            System.out.println(tuple.toString());
            write(tuple.toString(), true);
        }
        manager.deleteRelation(cartesian_join[ter]);
    } 

    public ArrayList<Tuple> createcrossjoin(ArrayList<Tuple> tuples1, ArrayList<Tuple> tuples2, String relation_name3, String[] relation_names, int name_index){
        ArrayList<String> Field_name = new ArrayList<>();
        ArrayList<FieldType> Field_type = new ArrayList<>();
        ArrayList<Tuple> tuple_collection = new ArrayList<>();
        
        Schema schema1 = tuples1.get(0).getSchema();
        Schema schema2 = tuples2.get(0).getSchema();
        
        ArrayList<String> list1 = schema1.getFieldNames();
        ArrayList<String> list2 = schema2.getFieldNames();

        //relationname1 has '.' in it
        if(!list1.get(0).contains(".")){
            for(int i = 0; i < list1.size(); i++){
                StringBuilder sb = new StringBuilder();
                sb.append(relation_names[0].toCharArray());
                sb.append('.');
                sb.append(list1.get(i).toCharArray());
                Field_name.add(sb.toString());
            }
        }
        else
            Field_name.addAll(list1);
        
        for(int i = 0; i < list2.size(); i++){
            StringBuilder sb = new StringBuilder();
            sb.append(relation_names[name_index].toCharArray());
            sb.append('.');
            sb.append(list2.get(i).toCharArray());
            Field_name.add(sb.toString());
        }
        
        Field_type.addAll(schema1.getFieldTypes());
        Field_type.addAll(schema2.getFieldTypes());
    
        Schema new_schema = new Schema(Field_name, Field_type);
        Relation new_relation = manager.createRelation(relation_name3, new_schema);
    
        for(int i = 0; i < tuples1.size(); i++){
            Tuple tuple1 = tuples1.get(i);
            ArrayList<Field> fields1 = new ArrayList<Field>();
            for (int k = 0; k < tuple1.getNumOfFields(); k++) {
                fields1.add(tuple1.getField(k));
            }
            for (int j = 0; j < tuples2.size(); j++){
                Tuple tuple2 = tuples2.get(j);
                ArrayList<Field> fields2 = new ArrayList<Field>();
                for (int p = 0; p < tuple2.getNumOfFields(); p++) {
                    fields2.add(tuple2.getField(p));
                }
                
                ArrayList<Field> new_fields = new ArrayList<Field>();
                new_fields.addAll(fields1);
                new_fields.addAll(fields2);
        
                Tuple new_tuple = manager.getRelation(relation_name3).createTuple();
                for (int q = 0; q < new_fields.size(); q++) {
                    if (new_fields.get(q).type == FieldType.INT) {
                        new_tuple.setField(q, new_fields.get(q).integer);
                    }
                    else{
                        new_tuple.setField(q, new_fields.get(q).str);
                    }
                }
                tuple_collection.add(new_tuple);
            }
        }
        return tuple_collection;
    }

    public void select_with_star_with_where(String[] relation_names, String[] where){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        ArrayList<Tuple> res = new ArrayList<Tuple>();
        int i = 0;
        for(i = 0; i < relation_names.length; i++){
            if (relation_names[i] == null) {
                break;
            }
        }
        ArrayList<String> rnames = new ArrayList<String>();
        for (int j = 0; j < i; j++) {
            rnames.add(relation_names[j]);
        }
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names[0]).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names[0]).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names[cj + 1]).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names[cj + 1]).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                    tuples2.add(block_for_2.getTuple(tuple_index_2));
                }
            }
            res = new ArrayList<Tuple>();
            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], relation_names, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
        }
        Schema schema = manager.getSchema(cartesian_join[ter]);
        for(int findex = 0; findex < schema.getNumOfFields(); findex++){
            System.out.print(schema.getFieldName(findex)+" ");
            write(schema.getFieldName(findex),false);
        }
        System.out.print("\n");
        write("", true);
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            if(whereparse(where, tuple)){
                System.out.println(tuple.toString()); 
                write(tuple.toString(),true);
            }                         
        }
            manager.deleteRelation(cartesian_join[ter]);
            
    }

    public void select_with_star_with_where_with_order(String[] relation_names, String[] where){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        ArrayList<Tuple> res = new ArrayList<Tuple>();
        int i = 0;
        for(i = 0; i < relation_names.length; i++){
            if (relation_names[i] == null) {
                break;
            }
        }
        ArrayList<String> rnames = new ArrayList<String>();
        for (int j = 0; j < i; j++) {
            rnames.add(relation_names[j]);
        }
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names[0]).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names[0]).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names[cj + 1]).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names[cj + 1]).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                    tuples2.add(block_for_2.getTuple(tuple_index_2));
                }
            }
            res = new ArrayList<Tuple>();
            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], relation_names, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
        }
        
        ArrayList<Tuple> result = new ArrayList<>();
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            if(whereparse(where, tuple)){
                result.add(tuple);   
            }                         
        }
        int len = tmp.length;
        String Field_name = tmp[len - 1];
        Collections.sort(result, new TupleComparator(Field_name));
        Schema schema = manager.getSchema(cartesian_join[ter]);
        for(int findex = 0; findex < schema.getNumOfFields(); findex++){
            System.out.print(schema.getFieldName(findex)+" ");
            write(schema.getFieldName(findex),false);
        }
        System.out.println();
        write("", true);
            for(int k = 0; k < result.size(); k++){
                Tuple t = result.get(k);
                System.out.println(t);
                write(t.toString(),true);
            }
           manager.deleteRelation(cartesian_join[ter]); 
            // String orderField = tmp[tmp.length - 1]; 
            // int relationSize =  manager.getRelation(Relation_name).getNumOfBlocks();
            // int memorySize = Config.NUM_OF_BLOCKS_IN_MEMORY;
            // if(relationSize > memorySize){
            //     SortPhaseI(Relation_name, Field_name);
            //     res = SortPhaseII(Relation_name, Field_name);
            // }
            // else{
            //   res = SortPhaseII(Relation_name, Field_name);
            // }
            // for(int j = 0; j < res.size(); j++){
            //     System.out.println(res.get(j));
            // }
            // return;
    }    
    
    public void select_no_star_no_where(ArrayList<String> attr_names, ArrayList<String> relation_names){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        int i = 0;
        ArrayList<Tuple> res = new ArrayList<Tuple>();
        ArrayList<String> rnames = new ArrayList<String>();
        rnames.addAll(relation_names);
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names.get(0)).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names.get(0)).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names.get(cj + 1)).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names.get(cj + 1)).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                  tuples2.add(block_for_2.getTuple(tuple_index_2));
                }  
            }
            res = new ArrayList<Tuple>();
            String[] replace = new String[relation_names.size()];
            for (int r = 0; r < relation_names.size(); r++) {
                replace[r] = relation_names.get(r);
            }
            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], replace, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
            
        }
        
        for(int findex = 0; findex < attr_names.size(); findex++){
            System.out.print(attr_names.get(findex)+" ");
            write(attr_names.get(findex),false);
        }
        System.out.println();
        write("", true);
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            for (int attribute_index = 0; attribute_index < attr_names.size(); attribute_index++) {
                Field toprint = tuple.getField(attr_names.get(attribute_index));
                System.out.print(toprint.toString() + '\t');
                write(toprint.toString(),false);
            }
            System.out.print('\n');
            write("",true);
        }
        manager.deleteRelation(cartesian_join[ter]);
    } 

    public void select_no_star_with_where(ArrayList<String> attr_names, ArrayList<String> relation_names, String[] where){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        int i = 0;
        ArrayList<Tuple> res = new ArrayList<Tuple>();;
        ArrayList<String> rnames = new ArrayList<String>();
        rnames.addAll(relation_names);
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names.get(0)).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names.get(0)).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names.get(cj + 1)).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names.get(cj + 1)).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                    tuples2.add(block_for_2.getTuple(tuple_index_2));
                }
            }
            res = new ArrayList<Tuple>();
            String[] replace = new String[relation_names.size()];
            for (int r = 0; r < relation_names.size(); r++) {
                replace[r] = relation_names.get(r);
            }

            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], replace, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
            
        }
        for(int findex = 0; findex < attr_names.size(); findex++){
            System.out.print(attr_names.get(findex)+" ");
            write(attr_names.get(findex),false);
        }
        System.out.println();
        write("", true);
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            if(whereparse(where, tuple)){
                for (int attribute_index = 0; attribute_index < attr_names.size(); attribute_index++) {
                    Field toprint = tuple.getField(attr_names.get(attribute_index));
                    System.out.print(toprint.toString() + '\t');
                    write(toprint.toString(),false);
                }
                System.out.print('\n');  
                write("",true);
            }                         
        }

        manager.deleteRelation(cartesian_join[ter]);
    }
    public void select_no_star_with_where_with_distinct(ArrayList<String> attr_names, ArrayList<String> relation_names, String[] where){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        int i = 0;
        ArrayList<Tuple> res = new ArrayList<Tuple>();;
        ArrayList<String> rnames = new ArrayList<String>();
        rnames.addAll(relation_names);
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names.get(0)).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names.get(0)).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names.get(cj + 1)).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names.get(cj + 1)).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                    tuples2.add(block_for_2.getTuple(tuple_index_2));
                }
            }
            res = new ArrayList<Tuple>();
            String[] replace = new String[relation_names.size()];
            for (int r = 0; r < relation_names.size(); r++) {
                replace[r] = relation_names.get(r);
            }
            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], replace, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
            
        }
        ArrayList<Tuple> result = new ArrayList<>();
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            if(whereparse(where, tuple)){
                result.add(tuple);   
            }                         
        }
        for(int findex = 0; findex < attr_names.size(); findex++){
            System.out.print(attr_names.get(findex)+" ");
            write(attr_names.get(findex),false);
        }
        System.out.println();
        write("", true);
        //System.out.println(result.size()); return;
        String Field_name1 = attr_names.get(0);
        String Field_name2 = attr_names.get(1);
        Collections.sort(result, new TupleComparator(Field_name1));
        Field last1 = result.get(0).getField(Field_name1);
        Field last2 = result.get(0).getField(Field_name2);
        System.out.print(result.get(0).getField(Field_name1) + "\t");
        write(result.get(0).getField(Field_name1).toString(), false);
        System.out.println(result.get(0).getField(Field_name2));
        write(result.get(0).getField(Field_name1).toString(), true);
        for(int k = 1; k < result.size(); k++){
            Tuple t = result.get(k);
            Field cur1 = t.getField(Field_name1);
            Field cur2 = t.getField(Field_name2);
            if(!isEqual(last1, cur1) || !isEqual(last2, cur2)){
                System.out.print(result.get(k).getField(Field_name1) + "\t");
                write(result.get(0).getField(Field_name1).toString(), false);
                System.out.println(result.get(k).getField(Field_name2));
                write(result.get(0).getField(Field_name2).toString(), true);
            }
            last1 = cur1;
            last2 = cur2;
        }


        manager.deleteRelation(cartesian_join[ter]);
    }
    public void select_no_star_with_where_with_DISTINCT_with_ORDER(ArrayList<String> attr_names, ArrayList<String> relation_names, String[] where){
        String[] cartesian_join = new String[]{"tmp1", "tmp2", "tmp3", "tmp4", "tmp5", "tmp6"};
        int i = 0;
        ArrayList<Tuple> res = new ArrayList<Tuple>();;
        ArrayList<String> rnames = new ArrayList<String>();
        rnames.addAll(relation_names);
        ArrayList<Tuple> tuples1 = new ArrayList<Tuple>();
        for (int b = 0; b < manager.getRelation(relation_names.get(0)).getNumOfBlocks(); b++) {
            manager.getRelation(relation_names.get(0)).getBlock(b, 1);
            Block block_for_1 = mem.getBlock(1);
            for(int tuple_index_1 = 0; tuple_index_1 < block_for_1.getNumTuples(); tuple_index_1++){
                tuples1.add(block_for_1.getTuple(tuple_index_1));
            }
        }
    
        int ter = 0;
        for (int cj = 0; cj < rnames.size() - 1; cj++) {
            ArrayList<Tuple> tuples2 = new ArrayList<Tuple>();
            for (int r = 0; r < manager.getRelation(relation_names.get(cj + 1)).getNumOfBlocks(); r++) {
                manager.getRelation(relation_names.get(cj + 1)).getBlock(r, 3);
                Block block_for_2 = mem.getBlock(3);
                for(int tuple_index_2 = 0; tuple_index_2 < block_for_2.getNumTuples(); tuple_index_2++){
                    tuples2.add(block_for_2.getTuple(tuple_index_2));
                }
            }
            res = new ArrayList<Tuple>();
            String[] replace = new String[relation_names.size()];
            for (int r = 0; r < relation_names.size(); r++) {
                replace[r] = relation_names.get(r);
            }

            res.addAll(createcrossjoin(tuples1, tuples2, cartesian_join[cj], replace, cj + 1));
            tuples1.clear();
            tuples1.addAll(res);
            if(cj > 0) manager.deleteRelation(cartesian_join[cj - 1]);
            ter = cj;
            
        }
        ArrayList<Tuple> result = new ArrayList<>();
        for(int j=0; j<res.size(); j++){
            Tuple tuple = res.get(j); 
            if(whereparse(where, tuple)){
                // for (int attribute_index = 0; attribute_index < attr_names.size(); attribute_index++) {
                //     Field toprint = tuple.getField(attr_names.get(attribute_index));
                //     System.out.print(toprint.toString() + '\t');
                // }
                // System.out.print('\n');
                result.add(tuple);
            }
        }
                for(int findex = 0; findex < attr_names.size(); findex++){
                    System.out.print(attr_names.get(findex)+" ");
                    write(attr_names.get(findex),false);
                }
                System.out.println();
                write("", true);
//                System.out.println(result.size());return;
                String Field_name1 = attr_names.get(0);
                String Field_name2 = attr_names.get(1);
                String Field_name3 = tmp[tmp.length - 1];
                Collections.sort(result, new TupleComparator(Field_name1));
                Field last1 = result.get(0).getField(Field_name1);
                Field last2 = result.get(0).getField(Field_name2);
                ArrayList<Tuple> Finalresult = new ArrayList<>();
                Finalresult.add(result.get(0));
                for(int k = 1; k < result.size(); k++){
                    Tuple t = result.get(k);
                    Field cur1 = t.getField(Field_name1);
                    Field cur2 = t.getField(Field_name2);
                    if(!isEqual(last1, cur1) || !isEqual(last2, cur2))
                        Finalresult.add(t);
                        last1 = cur1;
                        last2 = cur2;
            }
            Collections.sort(Finalresult,new TupleComparator(Field_name3));
                for(int p = 0; p < Finalresult.size(); p++){
                       System.out.println(Finalresult.get(p));
                       write(Finalresult.get(p).toString(), true);
                }
                //System.out.println("!!!");
        
        manager.deleteRelation(cartesian_join[ter]);
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
    
   public void tmpSELECT(String[] tmp){
        String Relation_name = tmp[2];
        Relation relation=manager.getRelation(Relation_name);
        int size = relation.getNumOfTuples(); int count = 0;
        ArrayList<String> fields = new ArrayList<>();
        for(int i = 3; i <= 7; i++){
            String str = tmp[i].replaceAll("(?i)[^a-zA-Z0-9.*\u4E00-\u9FA5]", "");
            fields.add(str);
        }
        int i = 4;
        for(int j=0; j<relation.getNumOfBlocks(); j++){
            relation.getBlock(j, 1);
            Block block = mem.getBlock(1);
            Block nb = mem.getBlock(2);
            for(int k=0; k < block.getNumTuples(); k++){
                Tuple tuple = block.getTuple(k); 
                Tuple newTuple = manager.getRelation(Relation_name).createTuple();
                copy(newTuple, tuple, fields);
        //Need to push the newly created tuple into the relation.
                nb.appendTuple(newTuple);
                relation.setBlock(i, 2);
                i++;
                nb.clear();
        //System.out.println(manager.getRelation(Relation_name).getNumOfTuples());
        //System.out.println(schema.getTuplesPerBlock());
            }
            
            
                count ++;
                if(count >= size) return;       
        }
}
   
    public void copy(Tuple t0, Tuple t1, ArrayList<String> fields){
        for(int i = 0; i < fields.size(); i++){
            Field field = t1.getField(fields.get(i));
            if(field.type == FieldType.STR20){
                t0.setField(fields.get(i), field.str);
            }
            else{
                t0.setField(fields.get(i),field.integer);
            }
        }
    }
 
    public void insert(){
        String Relation_name=tmp[2];
        int posOfValues=0;
        ArrayList<String> Field_name=new ArrayList<>();
        for(int i=3;i<tmp.length;i++){
            if(tmp[i].equals("SELECT")){
                tmpSELECT(tmp);
                return;
            }
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
            String fieldName = Field_name.get(i - posOfValues);
            int index = schema.getFieldNames().indexOf(fieldName);
            if(schema.getFieldTypes().get(index) == FieldType.STR20){
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
        boolean isFull = false;
        int sizeOfTuple = newTuple.getNumOfFields();
        int memory_block_index = 3;
        Block b = mem.getBlock(memory_block_index);
        if(lastRelation == null) b.clear();
        if( b.isFull() || !Relation_name.equals(lastRelation)){
            b.clear();
            isFull = true;
        }
        b.appendTuple(newTuple);
        int NumOfBlocks = manager.getRelation(Relation_name).getNumOfBlocks();
        if(isFull || NumOfBlocks == 0)
        manager.getRelation(Relation_name).setBlock(
            NumOfBlocks, 
            memory_block_index);
        else manager.getRelation(Relation_name).setBlock(   
            manager.getRelation(Relation_name).getNumOfBlocks() - 1, 
            memory_block_index);
        lastRelation = Relation_name;
        //System.out.println(manager.getRelation(Relation_name).getNumOfBlocks());
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
            boolean hasWhere=false; boolean hasORDER = false;
            for(int i=3;i<tmp.length;i++){
                if(tmp[i].equals("WHERE")){
                    hasWhere=true;
                    whereposition = i;
                    break;
                }
                relation_names[i-3]=tmp[i].replace(',',' ').trim();
            }
            for(int i = 0; i < tmp.length; i++){
                if(tmp[i].equals("ORDER"))
                    hasORDER = true;
            }
            if(!hasWhere && !hasORDER){
                if (relation_names[1] == null){//No where && No ORDER && Only One Relation                 
                    String Relation_name=relation_names[0]; 
                    Relation relation=manager.getRelation(Relation_name);
                    Schema schema = relation.getSchema();
                    for(int field_name_in = 0; field_name_in < schema.getNumOfFields(); field_name_in++){
                        System.out.print(schema.getFieldName(field_name_in)+" ");
                        write(schema.getFieldName(field_name_in), false);
                    }
                    write("", true);
                    System.out.print('\n');
                    for(int j=0; j<relation.getNumOfBlocks(); j++){
                        relation.getBlock(j, 1);
                        Block block = mem.getBlock(1);
                        for(int k=0; k<block.getNumTuples(); k++){
                            Tuple tuple = block.getTuple(k); 
                            System.out.println(tuple.toString()); 
                            write(tuple.toString(), true);
                        }
                    }                   
                }
                else{//No where && No ORDER && More than one relation
                    select_with_star_no_where(relation_names);
                }        
            }
            else if(!hasWhere && hasORDER){//No Where && has ORDER
                        String Relation_name = tmp[3];
                        String Field_name = tmp[6];
                        ArrayList<Tuple> res = new ArrayList<>();
                        int relationSize =  manager.getRelation(Relation_name).getNumOfBlocks();
                        int memorySize = Config.NUM_OF_BLOCKS_IN_MEMORY;
                        if(relationSize > memorySize){
                            SortPhaseI(Relation_name, Field_name);
                            res = SortPhaseII(Relation_name, Field_name);
                        }
                        else{
                            res = SortPhaseII(Relation_name, Field_name);
                        }
                        Schema schema = manager.getSchema(Relation_name);
                        for(int findex = 0; findex < schema.getNumOfFields(); findex++){
                            System.out.print(schema.getFieldName(findex)+" ");
                            write(schema.getFieldName(findex), false);
                        }
                        System.out.println();
                        write("", true);
                        for(int j = 0; j < res.size(); j++){
                            System.out.println(res.get(j));
                            write(res.get(j).toString(), true);
                        }
                        return;
            }
            else if(hasWhere && hasORDER){// Has Where && Has ORDER
                String[] where = new String[3];
                where[0] = tmp[6];
                where[1] = tmp[7];
                where[2] = tmp[8];
                select_with_star_with_where_with_order(relation_names, where);   
                // String orderField = tmp[tmp.length - 1]; 
                // int relationSize =  manager.getRelation(Relation_name).getNumOfBlocks();
                //     int memorySize = Config.NUM_OF_BLOCKS_IN_MEMORY;
                //     if(relationSize > memorySize){
                //             SortPhaseI(Relation_name, Field_name);
                //             res = SortPhaseII(Relation_name, Field_name);
                //         }
                //         else{
                //             res = SortPhaseII(Relation_name, Field_name);
                //         }
                //         for(int j = 0; j < res.size(); j++){
                //             System.out.println(res.get(j));
                //         }
                //         return;
            }
            else{// Only has Where
                String[] where = new String[tmp.length - whereposition - 1];
                for(int i = 0; i < where.length; i++){
                    where[i] = tmp[whereposition + i + 1];
                }
                if (relation_names[1] == null){
                    String Relation_name=relation_names[0];
                    Relation relation=manager.getRelation(Relation_name);
                    Schema schema = relation.getSchema();
                    for(int findex = 0; findex < schema.getNumOfFields(); findex++){                 
                        System.out.print(schema.getFieldName(findex)+" ");
                        write(schema.getFieldName(findex), false);
                    }
                    System.out.println();
                    write("", true);
                    for(int j=0; j<relation.getNumOfBlocks(); j++){
                        relation.getBlock(j, 1);
                        Block block = mem.getBlock(1);
                        for(int k=0; k<block.getNumTuples(); k++){
                            Tuple tuple = block.getTuple(k); 
                            if(whereparse(where, tuple)){
                                System.out.println(tuple.toString()); 
                                write(tuple.toString(), true);
                            }                         
                        }
                    }
                }
                else{
                    select_with_star_with_where(relation_names, where);
                }
            }                                                    
        }
        else if(str1.equals("DISTINCT")){//DISTINCT *
            String str2 = tmp[2];
            if(str2.equals("*")){
                String Relation_name = tmp[tmp.length - 1];
                Relation relation = manager.getRelation(Relation_name);
                int relationSize = relation.getNumOfBlocks();
                ArrayList<Tuple> res = new ArrayList<>();
                String Field_name = relation.getSchema().getFieldName(0);
                //Use the first filed to sort the tuples
                Schema schema = relation.getSchema();
                for(int findex = 0; findex < schema.getNumOfFields(); findex++){
                    System.out.print(schema.getFieldName(findex)+" ");                  
                    write(schema.getFieldName(findex),false);
                }
                System.out.print("\n");
                write("", true);
                if(relationSize < Config.NUM_OF_BLOCKS_IN_MEMORY){
                    res = SortPhaseII(Relation_name, Field_name);
                }
                else{
                    SortPhaseI(Relation_name, Field_name);
                    res = SortPhaseII(Relation_name, Field_name);
                }
                Tuple last = res.get(0);
                System.out.println(last);
                write(last.toString(), true);
                for(int i = 1; i < res.size(); i ++){
                    Tuple tmp = res.get(i);
                    for(int j = 0; j < tmp.getNumOfFields(); j ++){
                        Field a = last.getField(j);
                        Field b = tmp.getField(j);
                        if(!isEqual(a,b)){
                            System.out.println(tmp);
                            write(tmp.toString(), true);
                            last = tmp;
                            break;
                        }
                    }
                }               
            }
            // else if(str2.indexOf('.') != -1 ){
            //     String attribute = str2;
            //     String Relation_name = tmp[4];
            //     Relation relation = manager.getRelation(Relation_name);
            //         for(int j = 0; j < relation.getNumOfBlocks(); j++){
            //             relation.getBlock(j, 1);
            //             Block block = mem.getBlock(1);
            //             for(int k=0; k<block.getNumTuples(); k++){
            //                 Tuple tuple = block.getTuple(k);
            //                 System.out.println(tuple.getField(attribute).toString());    
            //             }
            //         }
            // }
            else if(tmp[3].equals("FROM")){// DISTINCT grade FROM course
                String Field_name = str2;
                String Relation_name = tmp[4];
                int relationSize = manager.getRelation(Relation_name).getNumOfBlocks();
                ArrayList<Tuple> res;
                System.out.println(str2);
                write(str2, true);
                if(relationSize <= Config.NUM_OF_BLOCKS_IN_MEMORY){
                    res = SortPhaseII(Relation_name, Field_name);
                }
                else{
                    SortPhaseI(Relation_name, Field_name);
                    res = SortPhaseII(Relation_name, Field_name);
                }
                Tuple last = res.get(0);
                System.out.println(last.getField(Field_name));
                write(last.getField(Field_name).toString(), true);
                for(int i = 1; i < res.size(); i ++){
                    Tuple tmp = res.get(i);
                    Field a = tmp.getField(Field_name);
                    Field b = last.getField(Field_name);
                    if(!isEqual(a,b)){
                        System.out.println(tmp.getField(Field_name));
                        write(tmp.getField(Field_name).toString(), true);
                    }
                    last = tmp;
                }   
            }
            else{
                boolean order = false;
                for(int i = 0; i < tmp.length; i++){
                    if(tmp[i].equals("ORDER")){
                        order = true;
                        break;
                    }
                }
                if(order){
                //SELECT DISTINCT course.grade, course2.grade FROM course, course2 WHERE course.sid = course2.sid AND [ course.exam > course2.exam OR course.grade = "A" AND course2.grade = "A" ] ORDER BY course.exam
                //System.out.println("YES");
                ArrayList<String> attr_names = new ArrayList<>();
                tmp[2] = tmp[2].substring(0, tmp[2].length() - 1);
                attr_names.add(tmp[2]);
                attr_names.add(tmp[3]);
                ArrayList<String> relation_names = new ArrayList<>();
                tmp[5] = tmp[5].substring(0, tmp[5].length() - 1);
                relation_names.add(tmp[5]);
                relation_names.add(tmp[6]);
                String[] where = new String[17];
                int wherepostion = 0;
                for(int i = 0; i < tmp.length; i++){
                    if(tmp[i].equals("WHERE")){
                        whereposition = i;
                        break;
                    }
                }
                for(int i = whereposition + 1; i < tmp.length; i ++){
                    if(tmp[i].equals("ORDER")) break;
                    where[i - whereposition - 1] = tmp[i];
                }
                select_no_star_with_where_with_DISTINCT_with_ORDER(attr_names, relation_names, where);
                }
                else{
                   ArrayList<String> attr_names = new ArrayList<>();
                   tmp[2] = tmp[2].substring(0, tmp[2].length() - 1);
                    attr_names.add(tmp[2]);
                    attr_names.add(tmp[3]);
                    String[] where = new String[3];
                int wherepostion = 0;
                for(int i = 0; i < tmp.length; i++){
                    if(tmp[i].equals("WHERE")){
                        whereposition = i;
                        break;
                    }
                }
                for(int i = whereposition + 1; i < tmp.length; i ++){
                    where[i - whereposition - 1] = tmp[i];
                }
                 ArrayList<String> relation_names = new ArrayList<>();
                tmp[5] = tmp[5].substring(0, tmp[5].length() - 1);
                relation_names.add(tmp[5]);
                relation_names.add(tmp[6]);
                
                select_no_star_with_where_with_distinct(attr_names, relation_names, where);
                
                }
            }  
        }
        else{//tmp[2] is not * or DISTINCT
                int x = 0;
                ArrayList<String> attribute_names = new ArrayList<String>();
                ArrayList<String> relation_names_no_star = new ArrayList<String>();
                boolean where_exist = false;
                for (x = 1; !tmp[x].equals("FROM"); x++) {
                    attribute_names.add(tmp[x].replace(',',' ').trim());
                }
                x = x + 1; //jump the "FROM"
                while(x < tmp.length && !tmp[x].equals("WHERE")){
                    relation_names_no_star.add(tmp[x].replace(',',' ').trim());
                    x++;
                }
                if (x < tmp.length) {
                    where_exist = true;
                    whereposition = x;
                }
                if (where_exist) {
                    String[] where = new String[tmp.length - whereposition - 1];
                    for(int i = 0; i < where.length; i++){
                        where[i] = tmp[whereposition + i + 1];
                    }
                                       
                    if (relation_names_no_star.size() == 1) {
                        for (int dis_point = 0; dis_point < attribute_names.size(); dis_point++) {
                            if (attribute_names.get(dis_point).contains(".")) {
                                String[] attr_names = attribute_names.get(dis_point).split("\\.");
                                attribute_names.set(dis_point, attr_names[1]);
                            }
                        }
                        String Relation_name=relation_names_no_star.get(0);
                        Relation relation=manager.getRelation(Relation_name);
                        for(int findex = 0; findex < attribute_names.size(); findex++){
                            System.out.print(attribute_names.get(findex)+" ");
                            write(attribute_names.get(findex),false);
                        }
                        System.out.println();
                        write("", true); 
                        for(int j=0; j<relation.getNumOfBlocks(); j++){
                            relation.getBlock(j, 1);
                            Block block = mem.getBlock(1);
                            for(int k=0; k<block.getNumTuples(); k++){
                                Tuple tuple = block.getTuple(k); 
                                if(whereparse(where, tuple)){
                                    //judge whether the field name matches
                                    for (int attribute_index = 0; attribute_index < attribute_names.size(); attribute_index++) {
                                        Field toprint = tuple.getField(attribute_names.get(attribute_index));
                                        System.out.print(toprint.toString() + '\t');
                                        write(toprint.toString(), false);
                                    }
                                    System.out.print('\n');   
                                    write("", true);
                                }                         
                            }
                        }                        
                    }
                    else{
                        select_no_star_with_where(attribute_names, relation_names_no_star, where);
                    }
                }
                else{
                    if (relation_names_no_star.size() == 1) {
                        for (int dis_point = 0; dis_point < attribute_names.size(); dis_point++) {
                            if (attribute_names.get(dis_point).contains(".")) {
                                String[] attr_names = attribute_names.get(dis_point).split("\\.");
                                attribute_names.set(dis_point, attr_names[1]);
                            }
                        }
                        String Relation_name=relation_names_no_star.get(0);
                        Relation relation=manager.getRelation(Relation_name);
                        for(int fin = 0; fin < attribute_names.size(); fin++){
                            System.out.print(attribute_names.get(fin)+" ");
                            write(attribute_names.get(fin), false);
                        }
                        System.out.println();
                        write(" ", true);
                        for(int j=0; j<relation.getNumOfBlocks(); j++){
                            relation.getBlock(j, 1);
                            Block block = mem.getBlock(1);
                            for(int k=0; k<block.getNumTuples(); k++){
                                Tuple tuple = block.getTuple(k); 
                                //judge whether the field name matches
                                for (int attribute_index = 0; attribute_index < attribute_names.size(); attribute_index++) {
                                    Field toprint = tuple.getField(attribute_names.get(attribute_index));
                                    System.out.print(toprint.toString() + '\t');
                                    write(toprint.toString(), false);
                                }
                                System.out.print('\n');
                                write("", true);
                            }
                        }                        
                    }
                    else{
                        select_no_star_no_where(attribute_names, relation_names_no_star);
                    }
                }
            }
    }
  
   
    public boolean isEqual(Field a , Field b){
        if(a.type == FieldType.INT){          
            return a.integer == b.integer;
        }
        else{
                return a.str.equals(b.str);
            }
    }
    public void SortPhaseI(String Relation_name, String Field_name){
        Relation relation = manager.getRelation(Relation_name);
        int memSize = Config.NUM_OF_BLOCKS_IN_MEMORY;
        int relationSize = relation.getNumOfBlocks();
        int i = 0; int j = 0;
        while(i < relationSize){
            while(j < memSize && i < relationSize){
                relation.getBlock(i, j);
                i ++; j ++;
            }
            quickSort(mem, Field_name);
            for(int k = 0; k < j; k++){
                relation.setBlock(i - j + k, k);
                mem.getBlock(k).clear();
            }
            j = 0;
        }
    }

    public void quickSort(MainMemory mem, String Field_name){
        ArrayList<Tuple> tmp = new ArrayList<>();
        for(int i = 0; i < Config.NUM_OF_BLOCKS_IN_MEMORY ;i++){
            Block b = mem.getBlock(i);
            if(b.isEmpty()) break;
            int size = b.getNumTuples();
            for(int j = 0; j < size; j++){
                tmp.add(b.getTuple(j));
            }
        }
        Collections.sort(tmp, new TupleComparator(Field_name));
    }

    public ArrayList<Tuple> SortPhaseII(String Relation_name, String Field_name){
        Relation relation = manager.getRelation(Relation_name);
        int memSize = Config.NUM_OF_BLOCKS_IN_MEMORY;
        int relationSize = relation.getNumOfBlocks();
        int i = 0; int j = 0; 
        ArrayList<Tuple> tmp = new ArrayList<>();
        while(i < relationSize){
            while(j < memSize && i < relationSize){
                relation.getBlock(i, j);
                i ++; j ++;
            }      
            for(int k = 0; k < j; k++){
                Block b = mem.getBlock(k);
                for(int m = 0; m < b.getNumTuples(); m++)
                tmp.add(b.getTuple(m));
                b.clear();
            }
            j = 0;
        }
        Collections.sort(tmp, new TupleComparator(Field_name));
        return tmp;
    }   
    


    public static void main(String[] args) throws IOException{
    Parser parser=new Parser();
    parser.readF1("/Users/jiaohongyang/Documents/csce608/TinySQL_linux.txt");
    
    }
}

class TupleComparator implements Comparator<Tuple>{
    String Field_name;
    public TupleComparator(String Field_name){
        this.Field_name = Field_name;
    }
    public int compare(Tuple a, Tuple b){
        if(a.getField(Field_name).type.equals(FieldType.INT)){
             int x = a.getField(Field_name).integer;
            int y = b.getField(Field_name).integer;
            return Integer.compare(x, y);
        }
        else
            return a.getField(Field_name).str.compareTo(b.getField(Field_name).str);
        }
}

