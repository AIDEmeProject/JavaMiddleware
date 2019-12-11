import * as d3 from 'd3'


class Dataset{

    static buildFromLoadedInput(fileContent){
        
        var csv = d3.csvParse(fileContent)        
        var dataset = new Dataset(csv)
        return dataset
    }

    constructor(d3dataset){
        this.dataset = d3dataset
        this.parsedColumns = {}
    }


    set_column_as_index(name){
        this.index = name
    }

    set_column_names_selected_by_user(names){
        
        if ( ! (typeof names[0] === "string")){
            this.selected_columns_for_exploration = names.map(e => e['name'])
        }
        else{
            this.selected_columns_for_exploration = names.map( e => e)
        }                           
    }


    get_point_with_index(id){

        if (! this.index){
            throw new Error("specify the column index before using this method")
        }

        var fetchedRow = this.dataset.filter(row => {
            return row[this.index] == id
        })

        return fetchedRow
    }

  
    get_raw_col_by_name(name){
        return this.dataset.map(e => e[name])
    }

    getLength(){
        return this.dataset.length
    }

    get_parsed_columns_by_names(names){
        var columns = names.map(name => {
            return this.get_column_name(name)
        })

        
        return d3.zip(...columns)
    }

    get_parsed_columns_by_id(ids){
        
        var columns = ids.map(id => {
            return this.get_parsed_column_by_id(id)
        })
        
        return d3.zip(...columns)
    }

    get_parsed_column_by_name(name){

        if ( ! (name in this.parsedColumns)){
            this.parsedColumns[name] = this.parse_string_column(name)
        }

        return this.parsedColumns[name]
    }

    get_parsed_column_by_id(id){

        const name = this._get_column_name_from_id(id)

        return this.get_parsed_column_by_name(name)
    }


    parse_string_column(name){

        var categories = {}
        var parsed = []
        var i = 0

        this.dataset.forEach(e => {
            var value = e[name]
            
            if ( ! (value in categories)){
                categories[value] = i
                i++
            }

            parsed.push(categories[value])
        })
                
        return parsed
    }
   
    _get_column_name_from_id(id){
        
        return this.dataset.columns[id]        
    }

    get_raw_column_by_id(id){
        
        var col_name =  this.get_column_names()[id]        
        var col = this.get_raw_col_by_name(col_name)
        
        return col
    }

    get_raw_column_by_name(name){
        
        return this.dataset.map( e => e[name])
    }

    get_column_id(id){
        
        var name = this.dataset.columns[id]
        return this.get_column_name(name)
    }

    get_column_name(name){
        
        const firstValue = parseFloat(this.dataset[0][name])
        const isValueNan = Number.isNaN(firstValue)
        
        if (isValueNan ){
            return this.get_parsed_column_by_name(name)
        }

        return this.dataset.map( e => parseFloat(e[name]))
    }


    get_column_names(){
        return this.dataset.columns
    }

    get_column_names_from_ids(ids){
        return ids.map(id => this.dataset.columns[id])
    }

    get_column_from_id(id){
        const name = this.get_column_id(id)
        
        return this.dataset.map(row => {
            return row[name]
        })
    }

    get_selected_columns_point(id){
        
        const row = this.dataset[id]
        
        var d = this.selected_columns_for_exploration.map(colName => {
            return row[colName]
        })
        
        return d
    }

    get_all_rows_with_selected_columns_by_user(){

        return this.dataset.map((e, i) => this.get_selected_columns_point(i))
    }
    

    get_point(id){
        
        const row = this.dataset[id]
    }
    
    get_columns(ids, aliases){

        const names = this.get_column_names()
        return this.dataset.map(row => {
            const d = []
            ids.forEach((id, i) => {
        
                const name = names[id]
                const alias = aliases[i]

                d[alias] =  row[name]                
            })

            return d            
        })
    }   

    uniqueValues(column_name){
        
        function uniqueValuesAsObject(arr){

            var counts = {};
            for (var i = 0; i < arr.length; i++) {
                counts[arr[i]] = 1 + (counts[arr[i]] || 0);
            }
    
            return counts
        }

        return uniqueValuesAsObject(this.get_raw_column_by_name(column_name))
    }

    min(column_name){
        return d3.min(this.get_parsed_column_by_name(column_name))
    }

    max(column_name){
        return d3.max(this.get_parsed_column_by_name(column_name))
    }

}


export default Dataset