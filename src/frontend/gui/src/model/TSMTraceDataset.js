import * as d3 from 'd3'

import Dataset from './Dataset'

class TSMTraceDataset extends Dataset{

    constructor(d3dataset){
        super(d3dataset)
    }

    static buildFromLoadedInput(fileContent, isCSV){
        
        var csv = isCSV ? d3.csvParse(fileContent) : d3.tsvParse(fileContent)
        var dataset = new TSMTraceDataset(csv)
        
        dataset.parse_trace()
        return dataset
    }

    get_point(id){
        return {
            id: this.point_indices[id],
            labels: this.labels[id]
        }
    }

    parse_trace(){

        this.labels = this.get_raw_col_by_name('labels').flatMap(s => {
            
            var cleanedStr = s.replace(/0.|0/g, "0,")
                              .replace(/1.|1/g, "1,")
                              .replace(/\n/g, ',')
                              .replace(/ /g, '')
                              
                              .replace(/,]/g, "]")            
                              //.replace(/$,/g, "]")
            
            
            return JSON.parse(cleanedStr)
            //return parseFloat(e.replace(/[\[\]']/g,'' ))                
        })
        
        this.point_indices = this.get_raw_col_by_name('labeled_indexes').flatMap(e => {
            return JSON.parse(e)
            //return parseFloat(e.replace(/[\[\]']/g,'' ))
        })    
    }
}

export default TSMTraceDataset