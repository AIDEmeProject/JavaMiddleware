/*
 * Copyright (c) 2019 École Polytechnique
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
 * If a copy of the MPL was not distributed with this file, you can obtain one at http://mozilla.org/MPL/2.0
 *
 * Authors:
 *       Luciano Di Palma <luciano.di-palma@polytechnique.edu>
 *       Enhui Huang <enhui.huang@polytechnique.edu>
 *       Laurent Cetinsoy <laurent.cetinsoy@gmail.com>
 *
 * Description:
 * AIDEme is a large-scale interactive data exploration system that is cast in a principled active learning (AL) framework: in this context,
 * we consider the data content as a large set of records in a data source, and the user is interested in some of them but not all.
 * In the data exploration process, the system allows the user to label a record as “interesting” or “not interesting” in each iteration,
 * so that it can construct an increasingly-more-accurate model of the user interest. Active learning techniques are employed to select
 * a new record from the unlabeled data source in each iteration for the user to label next in order to improve the model accuracy.
 * Upon convergence, the model is run through the entire data source to retrieve all relevant records.
 */

import * as d3 from "d3"

import * as hexbin from 'd3-hexbin'

class TwoDimensionHeatmapPlotter{
    
    prepare_plot(svgid, data){

        // set the dimensions and margins of the graph
        var margin = {top: 10, right: 30, bottom: 60, left: 80}
        var width = 650 - margin.left - margin.right
        var height = 500 - margin.top - margin.bottom

        // append the svg object to the body of the page
        var svg = d3.select(svgid)
            .attr("width", width + margin.left + margin.right)
            .attr('height', height + margin.top + margin.bottom)
            .append("g")
                .attr("transform",
                    "translate(" + margin.left + "," + margin.top + ")");
    

        var x = d3.scaleLinear()
             .domain(d3.extent(data, d => d[0]))
             .range([0, width])

        var y = d3.scaleLinear()
              .domain(d3.extent(data, d => d[1]))
              .range([height, 0])

       this.xAxis = svg.append("g")
                       .attr("transform", "translate(0," + height + ")")
                       .call(d3.axisBottom(x))

        this.yAxis = svg.append("g")
                        //.attr("transform", "translate(" + 25 + ", 0)")
                        .call(d3.axisLeft(y))

        this.gBins = svg.append("g")
                        .attr("stroke", "#000")
                        .attr("stroke-opacity", 0.1)

        this.xLabel = svg
                        .append("text")             
                            .attr("transform", "translate(" + (width/2) + " ," + 
                                 (height + margin.top + 30) + ")")
                            .style("text-anchor", "middle")
                            .style('fill', 'black')
            

          // text label for the y axis
        this.yLabel = svg.append("text")
                .attr("transform", "rotate(-90)")
                .attr("y", 0 - margin.left + 20)
                .attr("x",0 - (height / 2))
                .attr("dy", "1em")
                .style("text-anchor", "middle")
                .style('fill', 'black')
            
        

        this.svg = svg
        this.margin = margin
        this.x = x
        this.y = y
        this.height = height
        this.width = width        
    }
 
    plot(data, axisNames){

        var margin = this.margin,
            height = this.height,
            width = this.width,
            svg = this.svg,
            x = this.x,
            y = this.y,
            radius = 20
                             
            
        x.domain(d3.extent(data, d => d[0]))
        y.domain(d3.extent(data, d => d[1]))

        this.xAxis
            .transition()
            .duration(1000)
            .call(d3.axisBottom(x))


        var xLabel = axisNames[0]
        var yLabel = axisNames[1]
        this.xLabel.transition().text(xLabel)
        this.yLabel.transition().text(yLabel)

        this.yAxis
            .transition()
            .duration(1000)
            .call(d3.axisLeft(y))
              
        var yAxis = g => g
                .attr("transform", `translate(${margin.left},0)`)
                .call(d3.axisLeft(y).ticks(null, ".1s"))
                .call(g => g.select(".domain").remove())
                .call(g => g.append("text")
                    .attr("x", 4)
                    .attr("y", margin.top)
                    .attr("dy", ".71em")
                    .attr("fill", "currentColor")
                    .attr("font-weight", "bold")
                    .attr("text-anchor", "start")
                    .text(data.y))


        var xAxis = g => g
                .attr("transform", `translate(0,${height - margin.bottom})`)
                .call(d3.axisBottom(x).ticks(width / 80, ""))
                .call(g => g.select(".domain").remove())
                .call(g => g.append("text")
                    .attr("x", width - margin.right)
                    .attr("y", -4)
                    .attr("fill", "currentColor")
                    .attr("font-weight", "bold")
                    .attr("text-anchor", "end")
                    .text(data.x))      
              
        var binner = hexbin.hexbin()
                .x(d => x(d[0]))
                .y(d => y(d[1]))
                .radius(radius * width / 954)
                .extent([[margin.left, margin.top], [width - margin.right, height - margin.bottom]])
            
        var bins = binner(data)
        
        var color = d3.scaleSequential(d3.interpolateBuPu)
                      .domain([0, d3.max(bins, d => d.length) / 2])
                
        var update = this.gBins
           .selectAll("path")                   
           .data(bins)           

        update.exit().remove()

        update
           .enter()           
           .append('path')             
           .merge(update)
           .transition()
           .delay(1000)
           .attr("d", binner.hexagon())
           .attr("transform", d => `translate(${d.x},${d.y})`)
           .attr("fill", d => color(d.length));  
    }
}




export default TwoDimensionHeatmapPlotter