import React, { Component } from 'react';

import $ from "jquery";
import {backend} from '../../constants/constants'

//import init from '../../lib/sample_heatmap'

import * as  d3 from "d3"
class HeatMap extends Component{

    render(){
        return (
            <div >
                <div id="scatterplot" style={{minWidth: 500, minHeight: 500}}>
                    
                </div>
           
            </div>
        )
    }

    componentDidMount(){
        this.test2()
    }

    test2(){

        // set the dimensions and margins of the graph
        var margin = {top: 10, right: 30, bottom: 30, left: 60},
        width = 460 - margin.left - margin.right,
        height = 450 - margin.top - margin.bottom;

        // append the svg object to the body of the page
        var svg = d3.select("#scatterplot")
                    .append("svg")
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                    .attr("transform",
                        "translate(" + margin.left + "," + margin.top + ")");



        var xMin = 0,
            xMax = 10
        var yMin = 0,
            yMax = 10

        //Read the data
        var data = [
            {
                'id': 1,
                'label': 1,
                'data': [1, 2]
            },
            {
                'id': 2,
                'label': 1,
                'data': [4, 2]
            },
            {
                'id': 3,
                'label': 0,
                'data': [3, 1]
            },
        ]

        // Add X axis
        var x = d3.scaleLinear()
            .domain([xMin, xMax])
            .range([ 0, width ]);
        svg.append("g")
            .attr("transform", "translate(0," + height + ")")
            .call(d3.axisBottom(x));

        // Add Y axis
        var y = d3.scaleLinear()
                .domain([yMin, yMax])
                .range([ height, 0]);

        svg.append("g")
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
            tooltip
             .style("opacity", 1)
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

        // Add dots
        svg.append('g')
           .selectAll("dot")
           .data(data.filter(function(d,i){return i<50})) // the .filter part is just to keep a few dots on the chart, not all of them
           .enter()
           .append("circle")
           .attr("cx", function (d) { return x(d.data[0]); } )
           .attr("cy", function (d) { return y(d.data[1]); } )
           .attr("r", 7)
           .style("fill", function(d){ return d.label == 1 ? "green" : 'black'})
           
           .style("stroke", function(d){
                return d.label == 1 ? "green" : 'black'
            })
           .on("mouseover", mouseover )
           .on("mousemove", mousemove )
           .on("mouseleave", mouseleave)

            var xRange = d3.range(xMin, xMax, (xMax -xMin) / 30),
                yRange = d3.range(yMin, yMax, (yMax - yMin) / 30)
                

           var gridPoints = []

           xRange.forEach(x => {
               yRange.forEach(y => {
                
                    gridPoints.push(
                        {
                            data: [x, y],
                            label:  Math.floor(Math.random()* 2)
                        }
                    )
               })
           })


        // Add dots
        svg.append('g')
            .selectAll("dot")
            .data(gridPoints) 
            .enter()
            .append("circle")
            .attr("cx", function (d) { return x(d.data[0]); } )
            .attr("cy", function (d) { return y(d.data[1]); } )
            .attr("r", 3)
            .style("fill", function(d){ return d.label == 1 ? "green" : 'black'})
            .style("opacity", 0.3)
            .style("stroke", "white")
        
    }

    test1(){
        

        // set the dimensions and margins of the graph
        var margin = {top: 10, right: 30, bottom: 30, left: 60},
            width = 460 - margin.left - margin.right,
            height = 400 - margin.top - margin.bottom;

            // append the svg object to the body of the page
            var svg = d3.select("#scatterplot")
                        .append("svg")
                        .attr("width", width + margin.left + margin.right)
                        .attr("height", height + margin.top + margin.bottom)
                        .append("g")    
                        .attr("transform", "translate(" + margin.left + "," + margin.top + ")");

            //Read the data
           var data = [
               [1, 2],
               [2, 3],
               [3, 5]
           ]

            // Add X axis
            var x = d3.scaleLinear()
                .domain([0, 10])
                .range([ 0, width ]);
            svg.append("g")
                .attr("transform", "translate(0," + height + ")")
                .call(d3.axisBottom(x));

            // Add Y axis
            var y = d3.scaleLinear()
                .domain([0, 10])
                .range([ height, 0]);
            svg.append("g")
                .call(d3.axisLeft(y));
            console.log(data)
            // Add dots
            svg.append('g')
                .selectAll("dot")
                .data(data)
                .enter()
                .append("circle")
                .attr("cx", function (d) { return x(d[0]); } )
                .attr("cy", function (d) { return y(d[1]); } )
                .attr("r", 1.5)
                .style("fill", "black")






    }


}



export default HeatMap