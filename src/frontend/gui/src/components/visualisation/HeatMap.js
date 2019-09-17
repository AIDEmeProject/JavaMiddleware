import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../../constants/constants'

//import init from '../../lib/sample_heatmap'

import * as  d3 from "d3"
class HeatMap extends Component{

    constructor(props){
        super(props)    

        this.state = {

            scale: {
                xMin: 0,
                xMax: 10,
                yMin: 0,
                yMax: 10
            },
            firstVariable: 0,
            secondVariable: 1
        }
    }

    render(){
        const scale = this.state.scale
        console.log(scale)
        return (
            <div >
                <div id="scatterplot" style={{minWidth: 500, minHeight: 500}}>
                    
                </div>

                <div>

                    <select onChange={this.firstVariableChanged.bind(this) }>
                        {
                            this.props.availableVariables.map( (variable, i) => {
                                return ( 
                                    <option 
                                        value={i}
                                        data-value={i}                                        
                                    >
                                        {variable.name} {variable.realId}
                                    </option>
                                )
                            })
                        }
                    </select>

                    <label>
                    Minimum
                    <input                         
                        data-name="xMin"
                        value={scale.xMin}
                        onChange={this.onChangeScale.bind(this)} 
                    />
                    
                    </label>

                    <label>
                        Maximum                    
                        <input                             
                            data-name="xMax"
                            value={scale.xMax}
                            onChange={this.onChangeScale.bind(this)} 
                        />  
                    </label>
                </div>
                
                <div>
                    <select onChange={this.secondVariableChanged.bind(this) }>
                        {
                            this.props.availableVariables.map( (variable, i) => {
                                return (
                                    <option 
                                        
                                        data-value={variable.realId}
                                        value={i}
                                        key={i}
                                    >
                                        {variable.name} {variable.realId}
                                    </option>)
                            })
                        }
                    </select>

                    <input                         
                        data-name="yMin"
                        value={scale.yMin}
                        onChange={this.onChangeScale.bind(this)} 
                    />  
                    
                    <input                         
                        data-name="yMax"
                        value={scale.yMax}
                        onChange={this.onChangeScale.bind(this)} 
                    />  
                </div>                          
            </div>
        )
    }

    getChosenVariables(){
        return [this.state.firstVariable, this.state.secondVariable]
    }


    componentDidMount(){

        this.createPlot()
        this.plotData(this.svg, 
            this.state.scale, 
            this.width, 
            this.height, 
            this.props.labeledPoints, 
            this.props.gridPoints, 
            this.getChosenVariables())
        //this.updatePlot()
    }

    firstVariableChanged(e){
        this.setState({firstVariable: parseInt(e.target.value)}, this.updatePlot) 
    }

    secondVariableChanged(e){
        this.setState({secondVariable: parseInt(e.target.value)}, this.updatePlot) 
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
        var svg = d3.select("#scatterplot")
                    .append("svg")
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
        console.log(chosenVariables)
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
        tooltip
            .html("Label: " + d.label)
            .style("left", (d3.mouse(this)[0]+90) + "px") // It is important to put the +90: other wise the tooltip is exactly where the point is an it creates a weird effect
            .style("top", (d3.mouse(this)[1]) + "px")
        }

        // A function that change this tooltip when the leaves a point: just need to set opacity to 0 again
        var mouseleave = function(d) {
            tooltip
            .transition()
            .duration(200)
            .style("opacity", 0)
        }
        console.log(chosenVariables)

        var iFirstVariable = chosenVariables[0]
        var iSecondVariable = chosenVariables[1]
        console.log(labeledPoints)
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

var xRange = d3.range(0, 10, (10 - 0) / 10),
yRange = d3.range(0, 10, (10 - 0) / 10)

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

HeatMap.defaultProps = {

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

export default HeatMap