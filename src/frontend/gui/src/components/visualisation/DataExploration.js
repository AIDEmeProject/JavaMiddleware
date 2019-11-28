import React, { Component } from 'react';

import * as  d3 from "d3"


import Dataset from '../../model/Dataset'
import HistogramPlotter from './HistogramPlotter'
import OneDimensionHeatmapPlotter from './OneDimensionHeatmapPlotter'
import TwoDimensionHeatmapPlotter from './TwoDimensionHeatmapPlotter'
import VectorStatistics from './VectorStatistics';

class DataExploration extends Component{
    
    constructor(props){

        super(props)
        this.state = {
            firstVariable: props.firstVariable || 0,
            secondVariable: props.secondVariable,
            nBins: 10,
            min:0,
            max: 10
        }
    }

    render(){
                
        const iFirstVariable = this.state.firstVariable
        const iSecondVariable = this.state.secondVariable
    
        const dataset = this.props.dataset
        
        const variables = dataset.get_column_names()
        
        const firstVariable = dataset.get_column_id(iFirstVariable)
        const secondVariable = dataset.get_column_id(iSecondVariable)
                
        return (
            <div id="data-exploration">    
              

                <div>
                    <label htmlFor="min">Min</label>
                    <input 
                        className="form-input"
                        id="min"
                        type="number"
                        value={this.state.min} 
                        onChange={e => {this.setState({min: e.target.value}) } } 
                    />
                        
                    <label htmlFor="max">
                        Max
                    </label>

                        <input 
                        className="form-input"
                        id="max"
                        type="number"
                        value={this.state.max} 
                        onChange={e => {this.setState({max: e.target.value}) } } 
                    />                                                    
                </div>

               <VectorStatistics 
                    data={firstVariable}
               />
                               
                <div className="one-dimensional-plot">
                    <div>
                        <label htmlFor="bin-number">
                        Bin number <input 

                                className="form-input"
                                id="bin-number"
                                type="number"
                                value={this.state.nBins} 
                                onChange={e => {this.setState({nBins: e.target.value}) } } 
                            />
                            
                        </label>                        
                    </div>

                    <div>
                    
                        <svg id="histogram">
                        </svg>            

                       
                    </div>
                    <div>
                    <svg id="one-dimension-heatmap"></svg>
                    </div>
                    
                </div>

                <hr />

                <div>
                    <h2>
                        2D visualisation of {variables[iFirstVariable]} vs {variables[iSecondVariable]}
                    </h2>

                    <div className="variable-picker">

                        <select
                            value={this.state.firstVariable}
                            onChange={this.onFirstVariableChange.bind(this)}
                            className="form-control"
                        >
                            { 
                                variables.map((variable, i) => {
                                    return (<option key={i} value={i}>{variable}</option>)
                                }) 
                            }
                        </select>
                        
                        <select
                            value={this.state.secondVariable}
                            onChange={this.onSecondVariableChange.bind(this)}
                            className="form-control"
                        >
                            { 
                                variables.map((variable, i) => {
                                    return (<option value={i} key={i}>{variable}</option>)
                                })
                            }
                        </select>
                    </div>

                    <div>
                                    
                        <svg id="two-dimension-heatmap">
                        </svg>

                        <svg id="scatterplot">
                        </svg>

                    </div>                   
                </div>
            </div>
        )
    }

    componentWillReceiveProps(nextProps){
        
        this.setState({
            firstVariable: nextProps.firstVariable,
            secondVariable: nextProps.secondVariable
        })
    }

    onFirstVariableChange(e){
        this.setState({
            firstVariable: e.target.value
        })
    }

    onSecondVariableChange(e){
        this.setState({
            secondVariable: e.target.value
        })
    }

    getVariable(iVariable){
        const data = this.props.dataset.get_column_id(iVariable)
        return data
    }

   
    componentDidMount(){
        
        const data = this.getVariable(this.state.firstVariable)
        const dataset = this.props.dataset
        console.log(this.state.firstVariable, data)

        this.histogramPlotter = new HistogramPlotter()
        this.histogramPlotter.prepare_plot('#histogram', data)
        this.histogramPlotter.plot_histogram(data, this.state.nBins)
        
        this.oneDimensionHeatmapPlotter = new OneDimensionHeatmapPlotter()
        this.oneDimensionHeatmapPlotter.prepare_plot('#one-dimension-heatmap')
        this.oneDimensionHeatmapPlotter.plot(data, this.state.nBins)

        const df = dataset.get_columns([0, 1], ['x', 'y'])
        this.twoDimensionHeatmapPlotter = new TwoDimensionHeatmapPlotter()
        this.twoDimensionHeatmapPlotter.prepare_plot('#two-dimension-heatmap', df)        
        this.twoDimensionHeatmapPlotter.plot(df)


    }

    componentDidUpdate(){

        const data = this.getVariable(this.state.firstVariable)
        
        const dataset = this.props.dataset

        this.oneDimensionHeatmapPlotter.plot(data, this.state.nBins)
        this.histogramPlotter.plot_histogram(data, this.state.nBins)

        const iFirst = this.state.firstVariable
        const iSecond = this.state.secondVariable

        const df = dataset.get_columns([iFirst, iSecond], ['x', 'y'])
   
        this.twoDimensionHeatmapPlotter.plot(df)

    }
}


const data = [ 
    75.0, 104.0,369.0, 300.0, 92.0, 64.0, 265.0, 35.0, 287.0,
    69.0, 52.0,  23.0,  287.0, 87.0, 114.0, 114.0, 98.0, 137.0, 87.0, 90.0,   63.0,   69.0,  80.0,
    113.0,   58.0,   115.0,   30.0,   35.0,   92.0,   460.0,   74.0,   72.0,   63.0,  115.0,
    60.0,   75.0,   31.0 
]


var rawData = `carat,price
0.23,326
0.21,326
0.23,327
0.29,334
0.31,335
0.24,336
0.24,336
0.26,337
0.22,337
0.23,338
0.3,339
0.23,340
0.22,342
0.31,344
0.2,345
0.32,345
0.3,348
0.3,351
0.3,351
0.3,3512
1.13,5342
1.04,5344
1,5345
1.01,5345
1.01,5345
1.01,5345
1.01,5345
1.01,5345
1.02,5346
1.21,5346
1.02,5346
1.11,5346
1.1,5347
1.07,5347
1.05,5347
1.05,5347
1,5347
1.02,5350
1,5351
1,5351
1.05,5351
1.5,5351
1.2,5352
1.2,5352
1.05,5352
1.08,5352
1.08,5352
1.04,5353
1.03,5353
1.21,5353
1.06,5354
1.25,5355
1.25,5355
1.02,5356
1.03,5356
1.22,5356
0.9,5356
1.08,5357
1.34,535
1.2,5822
1.19,5822`
var dataset = d3.csvParse(rawData)




DataExploration.defaultProps = {
    
    'dataset': new Dataset(dataset)
}

export default DataExploration