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
            secondVariable: props.secondVariable || 1,
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
        
        const parsedFirstVariable = dataset.get_parsed_column_by_id(iFirstVariable)
        const firstVariable = dataset.get_column_id(iFirstVariable)

        var uniqueValues = Object.entries(this.uniqueValues(parsedFirstVariable))
        const rawUniqueValues = Object.entries(this.uniqueValues(dataset.get_column_id(iFirstVariable)))

        uniqueValues = d3.zip(rawUniqueValues, uniqueValues).map(e => {
            return [e[0][0], e[1][1]]
        })
        
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
                    uniqueValues={uniqueValues}
                    rawUniqueValues={rawUniqueValues}
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
                        <svg id="histogram"></svg>                                   
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
                        <svg id="two-dimension-heatmap"></svg>
                    </div>                   
                </div>
            </div>
        )
    }

    componentWillReceiveProps(nextProps){
        
        this.setState({
            firstVariable: nextProps.firstVariable,
        //    secondVariable: nextProps.secondVariable
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

   
    getParsedVariable(iVariable){
        return this.props.dataset.get_parsed_column_by_id(iVariable)
    }
    
    componentDidMount(){
        
        const data = this.getVariable(this.state.firstVariable)
        const dataset = this.props.dataset
        
        this.histogramPlotter = new HistogramPlotter()
        this.histogramPlotter.prepare_plot('#histogram', data)
        //this.histogramPlotter.plot_histogram(data, this.state.nBins)
        
        //this.oneDimensionHeatmapPlotter = new OneDimensionHeatmapPlotter()
        //this.oneDimensionHeatmapPlotter.prepare_plot('#one-dimension-heatmap')
        //this.oneDimensionHeatmapPlotter.plot(data, this.state.nBins)

        const heatmapData = dataset.get_parsed_columns_by_id([0, 1])
        this.twoDimensionHeatmapPlotter = new TwoDimensionHeatmapPlotter()
        this.twoDimensionHeatmapPlotter.prepare_plot('#two-dimension-heatmap', heatmapData)        
        //this.twoDimensionHeatmapPlotter.plot(df)

        this.plotAll()
    }

    uniqueValues(arr){

        var counts = {};
        for (var i = 0; i < arr.length; i++) {
            counts[arr[i]] = 1 + (counts[arr[i]] || 0);
        }

        return counts
    }

    plotAll(){
        const iFirstVariable = this.state.firstVariable
        const iSecondVariable = this.state.secondVariable

        const data = this.getVariable(iFirstVariable)        
        const dataset = this.props.dataset
        this.histogramPlotter.plot_histogram(data, this.state.nBins)

        
        const heatmapData = dataset.get_parsed_columns_by_id([iFirstVariable, iSecondVariable])        
        this.twoDimensionHeatmapPlotter.plot(heatmapData)
    }

    componentDidUpdate(){

        this.plotAll()

    }
}



export default DataExploration