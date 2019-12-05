import React, { Component } from 'react';

import $ from "jquery";
import * as  d3 from "d3"

import ModelBehaviorPlotter from './ModelBehaviorPlotter'


class LabelInfos extends Component{
    
    constructor(props){
        super(props)
    }

    render(){

        //const iteration = this.props.iteration
        const labeledPoints = this.props.labeledPoints
        
        const negativeSamples = labeledPoints.filter(e => e[2] === 0)
        const positiveSamples = labeledPoints.filter(e => e[2] === 1)

        return (

            <div id="iteration-labels">
                <div>
                    Labeled sample {
                        labeledPoints.length
                    }
                </div>

                <div>
                    Positive labels {
                        positiveSamples.length
                    }
                </div>

                <div>
                    Negative labels {
                        negativeSamples.length
                    }
                </div>
            </div>
        )
    }
}

class ModelBehaviorControls extends Component{

    constructor(props){
        super(props)
    }

    render(){

        const iteration = this.props.iteration

        return (
            <div id="iteration-control">

                <div>Iteration <span className="iteration-number">{iteration + 1}</span></div>
                <button
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onPreviousIteration}
                > Previous </button>
                <button
                    className="btn btn-primary btn-raised"
                    onClick={this.props.onNextIteration}
                >
                    Next
                </button>

            </div>
        )
    }
}

class ModelBehavior extends Component{

    constructor(props){

        super(props)    
        
        this.state = {
            gridPoints: [],
            modelIteration: 0,            
            firstVariable: 0,
            secondVariable: 1,
            scale: {
                xMin: -5,
                xMax: 5,
                yMin: -5,
                yMax: 5
            }
        }
    }

    render(){
        
        const scale = this.state.scale        
        const labeledPoints =  this.getHumanLabeledPoints()
        

        //const hasBehaviorData = this.props.history.length > 0 &&
        //                        this.props.gridHistory.labelHistory.length > 0

        //if ( ! hasBehaviorData){
        //    return (<div>Please label at least one point after the initial sampling phase. If it was done, please wait while vizualization data is computed</div>)
        //}

        return (

            <div className="row">
            
                <div className="col-lg-4 behavior-options">

                    <ModelBehaviorControls       
                        iteration={this.state.modelIteration}          
                        onPreviousIteration={this.onPreviousIteration.bind(this)}
                        onNextIteration={this.onNextIteration.bind(this)}
                    />

                    <LabelInfos
                        iteration={this.state.modelIteration}
                        labeledPoints={labeledPoints}
                    />

                <div className="form-inline">

                    <div className="form-group" >

                        <select 
                            value={this.state.firstVariable}
                            className="form-control inline" 
                            onChange={this.firstVariableChanged.bind(this) }
                        >
                            {
                                this.props.availableVariables.map( (variable, i) => {
                                    return ( 
                                        <option 
                                            key={i}
                                            className="form-control"
                                            value={i}
                                            data-value={i}                                        
                                        >
                                            {variable.name}
                                        </option>
                                    )
                                })
                            }
                        </select>                    
                    </div>

                    <br />

                    <div className="form-group">
                        <label htmlFor="xMin">
                        Minimum
                        </label>
                        <input          
                            id="xMin"               
                            data-name="xMin"
                            className="range-input"
                            value={scale.xMin}
                            onChange={this.onChangeScale.bind(this)} 
                        />
                    </div>
                    
                    <div className="form-group">

                        <label htmlFor="xMax">
                            Maximum     
                        </label>               
                        <input     
                            id="xMax"                        
                            data-name="xMax"
                            className="range-input"
                            value={scale.xMax}
                            onChange={this.onChangeScale.bind(this)} 
                        />  
                    </div>
                </div>
                
                <div className="form-inline">
                    <div className="form-group">

                        <select 
                            value={this.state.secondVariable}
                            className="form-control"
                            onChange={this.secondVariableChanged.bind(this) }
                        >
                            {
                                this.props.availableVariables.map( (variable, i) => {
                                    return (
                                        <option 
                                            className="form-control"
                                            data-value={variable.realId}
                                            value={i}
                                            key={i}
                                        >
                                            {variable.name}
                                        </option>
                                    )
                                })
                            }
                        </select>
                    </div>

                    <br />
                    
                    <div className="form-group">

                        <label htmlFor="yMin">
                            Minimum
                        </label>
                        <input      
                            id="yMin"                       
                            data-name="yMin"
                            value={scale.yMin}
                            className="range-input"
                            onChange={this.onChangeScale.bind(this)} 
                        />  
                    </div>

                    <div className="form-group">                    
                        <label htmlFor="yMax">
                            Maximum
                        </label>
                        <input                         
                            data-name="yMax"
                            value={scale.yMax}
                            className="range-input"
                            onChange={this.onChangeScale.bind(this)} 
                        />  
                    </div>
                </div>      
            </div>        
                <div className="col col-lg-8">
                    
                    <svg id="model-predictions-grid-point"></svg>

                    { this.props.hasTSM && <svg id="tsm-plot"></svg> }

                    <svg id="projection"></svg>                
                </div>        
            </div>
        )
    }

    
    componentWillMount(){
        
        const hasBehaviorData = this.props.modelPredictionHistory.length > 0 

        if ( ! hasBehaviorData){
            return
        }
        
        this.setState({
            scale: this.computeMinMaxOfRawData()
        })       
    }
    
    componentDidMount(){
        const columnNames = this.props.availableVariables.map( e => e.name)
        
        if (this.props.availableVariables.length <= 4){
            
            this.modelPredictionPlotter = new ModelBehaviorPlotter(columnNames)
            this.modelPredictionPlotter.createPlot('#model-predictions-grid-point', this.state.scale)
        }
        
        this.projectionPlotter = new ModelBehaviorPlotter(['X', 'Y'])
        this.projectionPlotter.createPlot("#projection", this.state.scale)


        if (this.props.hasTSM){
            this.tsmPlotter = new ModelBehaviorPlotter(columnNames)
            this.tsmPlotter.createPlot("#tsm-plot", this.state.scale)
        }
        
        this.plotAll()   
    }

    componentDidUpdate(){

        this.plotAll()            
    }

    plotAll(){
        
        if (this.props.availableVariables.length <= 4){
            this.plotPredictionOnGridPoints()
        }

        if (this.props.hasTSM){
            this.plotTSMPredictionsOnGridPoints()
        }

        this.plotDataEmbbedingPlot()
    }

    plotTSMPredictionsOnGridPoints(){

        var colors = {
            '-1': 'red',
            '0': 'grey',
            '1': 'green'
        }

        const scatterPoints = this.getTSMPredictionOverGridPoints()
        const chosenVariables = this.getChosenVariables()
        const humanLabeledPoints = this.getHumanLabeledPoints()
        const scale = this.state.scale
        
        this.tsmPlotter.plotData(scale, humanLabeledPoints, chosenVariables, scatterPoints, colors)
    }

    plotPredictionOnGridPoints(){
        
        var colors = {
            '-1': 'red',
            '0': 'grey',
            '1': 'green'
        }

        const scatterPoints = this.getModelPredictionOverGridPoints()
        
        const chosenVariables = this.getChosenVariables()
        const humanLabeledPoints = this.getHumanLabeledPoints()
        const scale = this.state.scale
        
        this.modelPredictionPlotter.plotData(scale, humanLabeledPoints, chosenVariables, scatterPoints, colors)
    }

    plotDataEmbbedingPlot(){

        var colors = {
            '-1': 'red',
            '0': 'grey',
            '1': 'green'
        }

        const embeddings = this.getEmbbedings()
        const x = embeddings.map(e => e[0])
        const y = embeddings.map(e => e[1])
        
        const scale = this.computeMinAndMaxScale(x, y)            
        const humanLabeledPoints = this.getLabeledEmbedding()
        
        const chosenVariables = [0, 1]
        this.projectionPlotter.plotData(
            scale,              
            humanLabeledPoints,
            chosenVariables,
            embeddings,             
            colors
        )        
    }


    getTSMPredictionOverGridPoints(){

        const iteration = this.state.modelIteration
        const modelPredictions = this.props.TSMPredictionHistory[iteration]
        const grid = this.props.fakePointGrid        
        
        const vars = this.getChosenVariables()
        const iColOne = vars[0]
        const iColTwo = vars[1]
        
        const gridPoints = d3.zip(grid, modelPredictions).map(e =>{
            const gridPoint = e[0]
            const prediction = e[1]
            
            return [gridPoint[iColOne], gridPoint[iColTwo], prediction.label]
        })
        
        return gridPoints

    }

    
    getModelPredictionOverGridPoints(){

        const iteration = this.state.modelIteration
        const modelPredictions = this.props.modelPredictionHistory[iteration]        
        const grid = this.props.fakePointGrid        

        

        const vars = this.getChosenVariables()
        const iColOne = vars[0]
        const iColTwo = vars[1]
        
        const scatter = d3.zip(grid, modelPredictions).map(e =>{
            const gridPoint = e[0]
            const prediction = e[1]
        
            return [gridPoint[iColOne], gridPoint[iColTwo], prediction.label]
        })
        
        return scatter
    }

    
    getEmbbedings(){
        
        const iteration = this.state.modelIteration

        return this.props.projectionHistory[iteration].embedding
    }

    getHumanLabeledPoints(){
                
        const iteration = this.state.modelIteration       
        var labeledPoints = this.props.labeledPoints.filter((e, i) =>{
            return i <= iteration
        })
                
        labeledPoints = labeledPoints.map(e => {            
            var v = e.data
            v.push(e.label)            
            return v
        })
        
        
        return labeledPoints
    }

    getLabeledEmbedding(){
                
        const embeddings = this.getEmbbedings()
        const iteration = this.state.modelIteration

        const labeledPoints = this.props.labeledPoints.filter((e, i) =>{
            return i <= iteration
        })
        
        const labeledEmbeddings = labeledPoints.map(e => {            
            return embeddings[e.id]
        })
        
        return labeledEmbeddings
    }
   
    onPreviousIteration(){

        var iteration = this.state.modelIteration - 1
        this.setState({
            modelIteration: Math.max(iteration, 0)
        })    
    }

    onNextIteration(){

        const nIteration = this.props.modelPredictionHistory.length
        var iteration = this.state.modelIteration + 1

        this.setState({
            modelIteration: Math.min(iteration, nIteration - 1)
        })        
    }
     
    computeMinMaxOfRawData(){
                
        const grid = this.getModelPredictionOverGridPoints()
        const offset = {x: 2, y:1}
        return this.computeMinAndMaxScale(grid.map(e => e[0]), grid.map(e => e[1]), offset)
    }

    computeMinAndMaxScale(xValues, yValues, offset ={x: 0, y:0}){

        var scale = {
            xMin: d3.min(xValues) - offset.x,
            xMax: d3.max(xValues) + offset.x,
            yMin: d3.min(yValues) - offset.y,
            yMax: d3.max(yValues) + offset.y
        }
        
        return scale
    }

    getChosenVariables(){

        const variables = [this.state.firstVariable, this.state.secondVariable]
        return variables
    }

    firstVariableChanged(e){
        var firstVariable = parseInt(e.target.value) 
        
        
        var newState = {
            firstVariable: firstVariable,
            scale: this.computeMinMaxOfRawData()
        }         
        this.setState(newState, this.plotAll) 
    }

    secondVariableChanged(e){
            
        
        var secondVariable = parseInt(e.target.value) 
        
        var newState = {
            secondVariable: secondVariable,
            scale: this.computeMinMaxOfRawData()
        }         

        this.setState(newState, this.plotAll) 
    }

    onChangeScale(e){

        var key = e.target.dataset.name
        var value = e.target.value
                
        if ( ! Number.isInteger(parseInt(value))){
            var newScale = Object.assign({}, this.state.scale, {[key]: value})    
            this.setState({
                scale: newScale    
            })
        }
        else{
            var newScale = Object.assign({}, this.state.scale, {[key]: parseInt(value)})

            this.setState({
                scale: newScale    
            }, this.plotGridPointPlot)                   
        }                                          
    } 
}

export default ModelBehavior