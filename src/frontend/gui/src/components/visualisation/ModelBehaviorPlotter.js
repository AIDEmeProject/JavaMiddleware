import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../../constants/constants'


import * as  d3 from "d3"
class ModelBehaviorPlotter extends Component{

    constructor(props){
        super(props)    
        
            
        this.state = {
            gridPoints: [],
            scale: this.computeMinAndMaxScale(0, 1),
            firstVariable: 0,
            secondVariable: 1
        }
    }

    render(){
        const scale = this.state.scale
        
        return (
            <div className="row">

                
                <div className="col-lg-5 behavior-options">

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
                                        </option>)
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

            <div className="col col-lg-6">
                    <svg id="scatterplot-svg"></svg>                
                </div>
        
            </div>
        )
    }

    componentDidMount(){
    
        this.createPlot()
        this.plotData(this.svg, 
            this.state.scale, 
            this.width, 
            this.height, 
            this.props.labeledPoints, 
            this.state.gridPoints, 
            this.getChosenVariables())
        //this.updatePlot()
    }

    getGridPoints(){

        var url = 'http://localhost:7060/label-fake-points-for-grid'

        $.ajax({
            type: "POST",
            dataType: 'JSON',
            url: url,
        }).then(response => {
            
            const gridPoints = response.map( e => {
                return {
                    data: e.dataPoint.data.array,
                    label: e.dataPoint.label === "POSITIVE" ? 1: 0
                }
            })
            
            this.setState({
                gridPoints: gridPoints
            }, () => {
                this.plotData(this.svg, 
                    this.state.scale, 
                    this.width, 
                    this.height, 
                    this.props.labeledPoints, 
                    this.state.gridPoints, 
                    this.getChosenVariables())
            })
        })
    }


    componentWillReceiveProps(nextProps){   
        

        const labeledPoints = nextProps.labeledPoints

        
        if (labeledPoints.length > 3){

            //this.getGridPoints()
            this.removeData(this.svg, labeledPoints, this.props.gridPoints)
            this.plotData(this.svg, 
                this.state.scale, 
                this.width, 
                this.height, 
                labeledPoints, 
                this.state.gridPoints, 
                this.getChosenVariables())
        }
    }

    computeMinAndMaxScale(iFirstVariable, iSecondVariable){

        const realIdOne = this.props.availableVariables[iFirstVariable].idx
        const realIdTwo = this.props.availableVariables[iSecondVariable].idx
        const datasetInfos = this.props.datasetInfos
        var scale = {
            xMin: datasetInfos.minimums[realIdOne],
            xMax: datasetInfos.maximums[realIdOne],
            yMin:  datasetInfos.minimums[realIdTwo],
            yMax: datasetInfos.maximums[realIdTwo]
        }
        return scale
    }

    getChosenVariables(){
        return [this.state.firstVariable, this.state.secondVariable]
    }


    firstVariableChanged(e){
        var firstVariable = parseInt(e.target.value) 
        var secondVariable = this.state.secondVariable
        
        var newState = {
            firstVariable: firstVariable,
            scale: this.computeMinAndMaxScale(firstVariable, secondVariable)
        }         
        this.setState(newState, this.updatePlot) 
    }

    secondVariableChanged(e){
            
        var firstVariable = this.state.firstVariable
        var secondVariable = parseInt(e.target.value) 
        
        var newState = {
            secondVariable: secondVariable,
            scale: this.computeMinAndMaxScale(firstVariable, secondVariable)
        }         

        this.setState(newState, this.updatePlot) 
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
            }, this.updatePlot)                   
        }                                          
    }
 
    createPlot(){

        // set the dimensions and margins of the graph
        var margin = {top: 20, right: 20, bottom: 20, left: 20},
        width = 500 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

        // append the svg object to the body of the page
        var svg = d3.select("#scatterplot-svg")
                    
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                    .attr("transform",
                        "translate(" + margin.left + "," + margin.top + ")");


        this.svg = svg
        this.width = width
        this.height = height

        return svg
    }

    removeData(svg, labeledPoints, gridPoints){
        
        svg            
           .selectAll("g")
           .data(labeledPoints)
           .exit()
           .remove()           

        svg            
           .selectAll("g")
           .data(gridPoints)
           .exit()
           .remove()                   
           
        svg            
           .selectAll("dot")
           .data(labeledPoints)
           .exit()
           .remove()        
    }


    updatePlot(){
        
        var chosenVariables = this.getChosenVariables()
        
        var x = this.x,
            y = this.y

        var scale = this.state.scale

        x.domain([scale.xMin, scale.xMax])
        this.xAxis.transition().duration(1000).call(d3.axisBottom(x))

        y.domain([scale.yMin, scale.yMax])
        this.yAxis.transition().duration(1000).call(d3.axisLeft(y))

        this.svg
            .selectAll("circle")
            .transition().duration(1000)
            .attr("cx", function (d) { return x(d.data[chosenVariables[0]]); } )
            .attr("cy", function (d) { return y(d.data[chosenVariables[1]]); } )
        
    }

    plotData(svg, scale, width, height, labeledPoints, gridPoints, chosenVariables){
                    
        var {xMin: xMin, xMax: xMax, yMin: yMin, yMax: yMax} = scale
        
        var x = d3.scaleLinear()
                  .domain([xMin, xMax])
                  .range([0, width]);
        this.x = x
            
                // Add Y axis
        var y = d3.scaleLinear()
                  .domain([yMin, yMax])
                  .range([height, 0]);

        this.y = y

        this.xAxis = svg.append("g")
            .attr("transform", "translate(0," + height  + ")")
            .call(d3.axisBottom(x));
 
        this.yAxis = svg.append("g")
            //.attr("transform", "translate(" + width / 2 + ", 0)")
            .call(d3.axisLeft(y));
      
        // Add a tooltip div. Here I define the general feature of the tooltip: stuff that do not depend on the data point.
        // Its opacity is set to 0: we don't see it by default.
        var tooltip = d3.select("#scatterplot")
                        .append("div")
                        .style("opacity", 0)
                        .attr("class", "tooltip")
                        .style("background-color", "white")
                        .style("border", "solid")
                        .style("border-width", "1px")
                        .style("border-radius", "5px")
                        .style("padding", "10px")

        // A function that change this tooltip when the user hover a point.
        // Its opacity is set to 1: we can now see it. Plus it set the text and position of tooltip depending on the datapoint (d)
        var mouseover = function(d) {
            tooltip.style("opacity", 1)
        }
        
        var mousemove = function(d) {

            //var x = d3.event.pageX - document.getElementById('scatterplot').getBoundingClientRect().x + 10
            //var y = d3.event.pageY - document.getElementById('scatterplot').getBoundingClientRect().y + 10

            var x = d3.mouse(this)[0] + 90
            var y = d3.mouse(this)[1] //+ document.getElementById('scatterplot').getBoundingClientRect().y

        tooltip
            .html("Row id: " + d.id + ". Label: " + d.label)
            .style("left", (x) + "px") // It is important to put the +90: other wise the tooltip is exactly where the point is an it creates a weird effect
            .style("top", (y)  + "px")
        }

        // A function that change this tooltip when the leaves a point: just need to set opacity to 0 again
        var mouseleave = function(d) {
            tooltip
            .transition()
            .duration(200)
            .style("opacity", 0)
        }
        

        var iFirstVariable = chosenVariables[0]
        var iSecondVariable = chosenVariables[1]
        
        // Add dots
        svg.append('g')
           .selectAll("dot")
           .data(labeledPoints)
           .enter()
           .append("circle")
           .attr("cx", function (d) { return x(d.data[iFirstVariable]); } )
           .attr("cy", function (d) { return y(d.data[iSecondVariable]); } )
           .attr("r", 7)
           .style("fill", function(d){ return d.label == 1 ? "green" : 'black'})
           
           .style("stroke", function(d){
                return d.label == 1 ? "green" : 'black'
            })

           .on("mouseover", mouseover )
           .on("mousemove", mousemove )
           .on("mouseleave", mouseleave)
           .exit()
           .remove()          

        // Add dots
        svg.append('g')
            .selectAll("dot")
            .data(gridPoints) 
            .enter()
            .append("circle")
            .attr("cx", function (d) { return x(d.data[iFirstVariable]); } )
            .attr("cy", function (d) { return y(d.data[iSecondVariable]); } )
            .attr("r", 3)
            .style("fill", function(d){ return d.label == 1 ? "green" : 'black'})
            .style("opacity", 0.3)
            .style("stroke", "white")
            .exit()
            .remove()    
    }
}

var xRange = d3.range(0, 40, (40 - 0) / 10),
yRange = d3.range(0, 40, (40 - 0) / 10)

var gridPoints = []
xRange.forEach(x => {

    yRange.forEach(y => {   
    
        xRange.forEach(z => {
            
                gridPoints.push({
                    data: [x, y, z],
                    label:  Math.floor(Math.random()* 2)
                })            
        })
    })
})

ModelBehaviorPlotter.defaultProps = {

    labeledPoints: [
        {
            'id': 1,
            'label': 1,
            'data': [1, 2, 1, 0]
        },
        {
            'id': 2,
            'label': 1,
            'data': [4, 2, -1, 3]
        },
        {
            'id': 3,
            'label': 0,
            'data': [3, 1, 1, 2]
        },
    ],
    gridPoints: gridPoints,
    availableVariables: [
        {
            'name': 'test1',
            'realId': 0
        },

        {
            'name': 'test2',
            'realId': 1
        },
        
        {
            'name': 'test3',
            'realId': 2
        },

    ]


}

export default ModelBehaviorPlotter