import * as d3 from 'd3'

import Dataset from './Dataset'

class TraceDataset extends Dataset{

    constructor(d3dataset){
        super(d3dataset)
    }

    static buildFromLoadedInput(fileContent, isCSV){
        
        var csv = isCSV ? d3.csvParse(fileContent) : d3.tsvParse(fileContent)
        var dataset = new TraceDataset(csv)

        dataset.parse_trace()
        return dataset
    }

    parse_initial_sampling(){

    }

    get_point(id){
        return {
            id: this.point_indices[id],
            label: this.labels[id]
        }
    }

    parse_trace(){
        
        this.labels = this.get_raw_col_by_name('labels').flatMap(e => {
            var cleanedStr = e.replace("0.", "0").replace("1.", "1").replace(' ', ',')
            
            return JSON.parse(cleanedStr)
            //return parseFloat(e.replace(/[\[\]']/g,'' ))                
        })
        

        this.point_indices = this.get_raw_col_by_name('labeled_indexes').flatMap(e => {
            return JSON.parse(e)
            //return parseFloat(e.replace(/[\[\]']/g,'' ))
        })        
    }
}

export default TraceDataset