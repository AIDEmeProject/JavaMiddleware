import * as  d3 from "d3"

class ModelBehaviorPlotter{

    createPlot(svgId, scale){

        // set the dimensions and margins of the graph
        var margin = {top: 20, right: 20, bottom: 20, left: 20},
        width = 500 - margin.left - margin.right,
        height = 500 - margin.top - margin.bottom;

        // append the svg object to the body of the page
        var svg = d3.select(svgId)                    
                    .attr("width", width + margin.left + margin.right)
                    .attr("height", height + margin.top + margin.bottom)
                    .append("g")
                    .attr("transform",
                        "translate(" + margin.left + "," + margin.top + ")");

        this.svg = svg
        this.width = width
        this.height = height

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
        
        // Add dots
        var gLabels = svg.append('g')
            .attr('class', 'labels')
        
        this.gLabels = gLabels

        this.gPredictions = svg.append('g')
            .attr('class', 'predictions')
            
        this.tooltip = this.svg
            .append("g")
                .attr('class', 'tooltip-behavior')
            
                .style("opacity", 1)                
                .style("position", 'absolute')  
                .style("background-color", "white")
                .style("border", "solid")
                .style("border-width", "1px")
                .style("border-radius", "5px")
                .style("padding", "10px")

        this.tooltip = d3.select("body").append("div")
                .attr("id", "model-behavior-tooltip")
                .style("opacity", 0);

        return svg
    }

    plotData( scale, humanLabeledPoints, scatterPoints, chosenVariables, colors){
                
        var iFirstVariable = chosenVariables[0]
        var iSecondVariable = chosenVariables[1]

        const svg = this.svg        
        const x = this.x
        const y = this.y
                             
        this.updateAxis(x, y, scale)
        // Add a tooltip div. Here I define the general feature of the tooltip: stuff that do not depend on the data point.
        // Its opacity is set to 0: we don't see it by default.
        
        // A function that change this tooltip when the user hover a point.
        // Its opacity is set to 1: we can now see it. Plus it set the text and position of tooltip depending on the datapoint (d)
        var mouseover = (d) => {
            this.tooltip.style("opacity", 1)
        }
        
        var mousemove = (d) => {

           // var x = d3.event.pageX - document.getElementById('scatterplot').getBoundingClientRect().x + 10
            //var y = d3.event.pageY - document.getElementById('scatterplot').getBoundingClientRect().y + 10

            //var x = d3.mouse(this)[0] + 90
            //var y = d3.mouse(this)[1] //+ document.getElementById('scatterplot').getBoundingClientRect().y
            
            var xTooltip = d3.event.pageX + 30
            var yTooltip = d3.event.pageY 
            this.tooltip
                .html( "Data : " + d + "<br />Model prediction: " + d.label)
                .style("left", (xTooltip) + "px") // It is important to put the +90: other wise the tooltip is exactly where the point is an it creates a weird effect
                .style("top", (yTooltip)  + "px")
        }

        // A function that change this tooltip when the leaves a point: just need to set opacity to 0 again
        var mouseleave = (d) => {
            this.tooltip
                .transition()
                .duration(200)
                .style("opacity", 0)
        }
                                
        // Add dots
        var updatePrediction = this.gPredictions            
            .selectAll("circle")
            .data(scatterPoints)     
                
        updatePrediction.exit().remove()
        
        updatePrediction
            .enter()
            .append("circle")
            .merge(updatePrediction)
            .on("mouseover", mouseover)
            .on("mousemove", mousemove)
            .on("mouseleave", mouseleave)              
            .transition(500)            
            .attr("cx", function (d) { return x(d[iFirstVariable]); } )
            .attr("cy", function (d) { return y(d[iSecondVariable]); } )
            .attr("r", 4)
            .style("fill", function(d){ return colors[d[2]] })
            .style("opacity", 0.4)
            .style("stroke", "white") 

        var updateLabels = this.gLabels
            .selectAll("circle")
            .data(humanLabeledPoints)
     
        updateLabels
            .enter()
            .append("circle")
            .merge(updateLabels)
            .transition()
            .delay(500)    
            .attr("cx", (d) => { return x(d[iFirstVariable]) } )
            .attr("cy", (d) => { return y(d[iSecondVariable]) } )
            .attr("r", 7)
            .style("fill", function(d){ return d[2] == 1 ? "green" : 'red'})           
            .style("stroke", function(d){
                    return d[2] == 1 ? "green" : 'red'
            })
                    
        updateLabels.exit().remove()                     
    }

    updateAxis(x, y, scale){

        const transitionLength = 200
        x.domain([scale.xMin, scale.xMax])
        this.xAxis.transition().duration(transitionLength).call(d3.axisBottom(x))

        y.domain([scale.yMin, scale.yMax])
        this.yAxis.transition().duration(transitionLength).call(d3.axisLeft(y))
    }
}

export default ModelBehaviorPlotter