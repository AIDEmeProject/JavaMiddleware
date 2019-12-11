import * as d3 from "d3"

class HistogramPlotter{
    
    prepare_plot(svgid, data){

        // set the dimensions and margins of the graph
        var margin = {top: 10, right: 30, bottom: 100, left: 50}
        var width = 800 - margin.left - margin.right
        var height = 650 - margin.top - margin.bottom

        // append the svg object to the body of the page
        var svg = d3.select(svgid)                
                .attr("width", width + margin.left + margin.right)
                .attr("height", height + margin.top + margin.bottom)
                .append("g")
                .attr("transform",
                    "translate(" + margin.left + "," + margin.top + ")");

        // X axis: scale and draw:
        var x = d3.scaleLinear()
                  .domain([0, d3.max(data)])     // can use this instead of 1000 to have the max of data: d3.max(data, function(d) { return +d.price })
                  .range([0, width]);


        // Y axis: initialization
        var y = d3.scaleLinear()
                  .range([height, 0]);

        var yAxis = svg.append("g")
        

        this.xAxis = svg.append("g")
           .attr("transform", "translate(0," + height + ")")
           .call(d3.axisBottom(x))
           
        this.svg = svg
        this.x = x
        this.y = y

        this.yAxis = yAxis
    
        this.height = height
        this.width = width        
    }
 
    plot_histogram(data, nBins, isCategorical){

        var svg = this.svg
        var x = this.x,
            y = this.y,
            yAxis = this.yAxis
        
        var height = this.height,
            width = this.width
                
        if (isCategorical){

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
                    .html( "Category : " + d[0] + "<br />Count: " + d[1])
                    .style("left", (xTooltip) + "px") // It is important to put the +90: other wise the tooltip is exactly where the point is an it creates a weird effect
                    .style("top", (yTooltip)  + "px")
            }
    
            // A function that change this tooltip when the leaves a point: just need to set opacity to 0 again
            var mouseleave = (d) => {
                this.tooltip
                    
                    
                    .style("opacity", 0)
            }
    
            data = data.filter((e, i ) => i < nBins)
            
            var nBars = Math.min(data.length, nBins)
            var barSize = width / nBars
            var total = data.reduce((a, e) => a + e[1], 0)
            
            //var domain = d3.extent(data.map(e => e[1]))                        
            x.domain([0, nBars])
            
            var ticks = data.map(e => e[0])
            //console.log(ticks)
            var xAxis = d3.axisBottom(x)
                          .tickValues(d3.range(nBars))
                          .tickFormat((d, i) => ticks[i])
                          
              
            this.xAxis.transition()
                .duration(1000)
                .call(xAxis)
                .selectAll('text')
                    .style("text-anchor", "end")
                    .style('dx', '.75em')
                    .attr("transform", "rotate(-65)");

            y.domain([0, total]);  
            yAxis
                .transition()
                .duration(1000)
                .call(d3.axisLeft(y)) 

            var r = svg.selectAll("rect")
                       .data(data)

            r.enter()
             .append("rect")
             .merge(r)
             .on("mouseover", mouseover)
             .on("mousemove", mousemove)
             .on("mouseleave", mouseleave) 
             .transition()
             .duration(1000)
                .attr("x", 1)
                .attr("transform", (d, i) => { 
                    
                    var barHeight = d[1]                     
                    var xTranslate = i * barSize,
                        yTranslate = y(barHeight)
                        //yTranslate = 0                                    
                    return "translate(" + xTranslate + "," + yTranslate + ")"; 
                })
                .attr("width", function(d) { return barSize ; })
                .attr("height", function(d) { 
                    
                    return height - y(d[1]); 
                })
                .style("fill", "#2574b5")
                .style('stroke', 'white')
        
            r.exit().remove()
        }
        else{
    
            x.domain([d3.min(data) - 1, d3.max(data) + 1])

            this.xAxis.transition()
                .duration(1000)
                .call(d3.axisBottom(x))
                .selectAll('text')
                .style("text-anchor", "end")
                .attr("dx", "-.8em")
                .attr("dy", ".15em")
                .attr("transform", "rotate(-65)")

                // set the parameters for the histogram
            var histogram = d3.histogram()
                .value(function(d) { return d; })   // I need to give the vector of value
                .domain(x.domain())  // then the domain of the graphic
                .thresholds(x.ticks(nBins)); // then the numbers of bins

            // And apply this function to data to get the bins
            var bins = histogram(data);
                
            y.domain([0, d3.max(bins, function(d) { return d.length; })]);   // d3.hist has to be called before the Y axis obviously
            yAxis
                .transition()
                .duration(1000)
                .call(d3.axisLeft(y))

                // append the bar rectangles to the svg element
                var r = svg.selectAll("rect")
                .data(bins)

            r
                .enter()
                .append("rect")
                .merge(r)
                .transition()
                .duration(1000)
                    .attr("x", 1)
                    .attr("transform", function(d) { return "translate(" + x(d.x0) + "," + y(d.length) + ")"; })
                    .attr("width", function(d) { return x(d.x1) - x(d.x0) -1 ; })
                    .attr("height", function(d) { return height - y(d.length); })
                    .style("fill", "#2574b5")
                
                r.exit().remove()
        }
    }
}


export default HistogramPlotter