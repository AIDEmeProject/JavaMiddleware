import * as d3 from "d3"

import * as hexbin from 'd3-hexbin'

class TwoDimensionHeatmapPlotter{
    
    prepare_plot(svgid, data){

        // set the dimensions and margins of the graph
        var margin = {top: 30, right: 30, bottom: 30, left: 30}
        var width = 700 - margin.left - margin.right
        var height = 500 - margin.top - margin.bottom

        // append the svg object to the body of the page
        var svg = d3.select(svgid)
            .attr("width", width + margin.left + margin.right)
            .attr('height', height + margin.top + margin.bottom)

        /*
        // X axis: scale and draw:
        var x = d3.scaleLinear()
                  .domain([0, 20])     // can use this instead of 1000 to have the max of data: d3.max(data, function(d) { return +d.price })
                  .range([0, width]);


        var y = d3.scaleLinear()
                  .domain([0, 20])
                  .range([height, 0]);

        svg.append("g")
           .attr("transform", "translate(0," + height + ")")
           .call(d3.axisBottom(x));


        svg.append("g")
           .attr("transform", "translate(" + 0 + ", 0)")
           .call(d3.axisLeft(y));

        */

        var x = d3.scaleLinear()
             .domain(d3.extent(data, d => d.x))
             .range([0, width])

        var y = d3.scaleLinear()
              .domain(d3.extent(data, d => d.y))
              .range([height, 0])

       this.xAxis = svg.append("g")
                       .attr("transform", "translate(0," + height + ")")
                       .call(d3.axisBottom(x))

        this.yAxis = svg.append("g")
                        .call(d3.axisLeft(y))

        this.gBins = svg.append("g")
                        .attr("stroke", "#000")
                        .attr("stroke-opacity", 0.1)

        this.svg = svg
        this.margin = margin
        this.x = x
        this.y = y
        this.height = height
        this.width = width        
    }
 
    plot(data){

        var margin = this.margin,
            height = this.height,
            width = this.width,
            svg = this.svg,
            x = this.x,
            y = this.y,
            radius = 20
                             
            
        x.domain(d3.extent(data, d => d.x))
        y.domain(d3.extent(data, d => d.y))

        this.xAxis
            .transition()
            .duration(1000)
            .call(d3.axisBottom(x))

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
                .x(d => x(d.x))
                .y(d => y(d.y))
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

    plotold(data){

        var svg = this.svg    
        var x = this.x
        var y = this.y

        var height = this.height
        var heat_color = d3.scaleLinear().domain([0, 100]).range(["red", "yellow"]);

        var one_d_heatmap_margin = {top: 20, right: 20, bottom: 40, left: 60},
            legend_margin = {top: 10, right: 40, bottom: 20, left: 40},
            legend_width = 40,
            
            value_range = {
                x: {
                    min_value: 0,
                    max_value: 600
                },
                y: {
                    min_value: 0,
                    max_value: 600
                }
            }     
               
        var heatmap_height = this.height

        svg.selectAll("rect")
            .data(data)
            .enter()
            .append("rect")
            .attr("class", "rectangles")
            .attr("x", function(d, i) {
                return x(d.x);
            })
            .attr("y", function(d, i) {
                return y(d.y);
            })
            .attr("width", function(d, i) {
                return x(d.width);
            })
            .attr("height", function(d, i) {
                return y(d.height);
            })
            .attr("fill", function(d, i) {
                
                if(! d.aggValue){                    
                    return "White";
                }                
                    
                return heat_color(d.aggValue);	                    
            })
            .attr("stroke", "#fff")
            .attr("stroke-width", 1)            
    }
}




export default TwoDimensionHeatmapPlotter