import * as d3 from 'd3'

class Dataset{


    static buildFromLoadedInput(fileContent){
        
        var csv = d3.csvParse(fileContent)        
        var dataset = new Dataset(csv)
        return dataset
    }
    constructor(d3dataset){
        this.dataset = d3dataset
    }

    set_columns_selected_by_users(names){
        this.select_column_names = names.map(e => e['name'])
    }

    get_column_name(name){

        return this.dataset.map( e => parseFloat(e[name]))
    }

    get_column_id(id){
        var name = this.dataset.columns[id]
        return this.get_column_name(name)
    }

    get_column_names(){
        return this.dataset.columns
    }

    get_column_from_id(id){
        const name = this.get_column_id(id)
        
        return this.dataset.map(row => {
            return row[name]
        })
    }

    get_point(id){
        
        const row = this.dataset[id]
        var d = this.select_column_names.map(e => {
            return row[e]
        })
        
        return d
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

    compute_statistics(){
        var statistics = []
        this.dataset.columns.forEach(e => {
            var column = this.get_column_name
        })
    }

    generate_grid_point(columnsIds){

    }
}


export default Dataset