import * as d3 from "d3"

class HistogramPlotter{
    
    prepare_plot(svgid, data){

        // set the dimensions and margins of the graph
        var margin = {top: 10, right: 30, bottom: 70, left: 50}
        var width = 800 - margin.left - margin.right
        var height = 600 - margin.top - margin.bottom

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
        x.domain([d3.min(data) - 1, d3.max(data) + 1])
        
        // set the parameters for the histogram
        var histogram = d3.histogram()
            .value(function(d) { return d; })   // I need to give the vector of value
            .domain(x.domain())  // then the domain of the graphic
            .thresholds(x.ticks(nBins)); // then the numbers of bins

        // And apply this function to data to get the bins
        var bins = histogram(data);
        console.log(bins)
        
        this.xAxis.transition()
            .duration(1000)
            .call(d3.axisBottom(x))
            .selectAll('text')
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".15em")
            .attr("transform", "rotate(-65)")
           
             
        y.domain([0, d3.max(bins, function(d) { return d.length; })]);   // d3.hist has to be called before the Y axis obviously
        yAxis
            .transition()
            .duration(1000)
            .call(d3.axisLeft(y))

        if (isCategorical){

            var barSize = width / data.length
            var total = data.reduce((a, e) => a + e[1], 0)
            
            y.domain([0, total]);   

            var r = svg.selectAll("rect")
                       .data(data)

            r.enter()
             .append("rect")
             .merge(r)
             .transition()
             .duration(1000)
                .attr("x", 1)
                
                .attr("transform", (d, i) => { 
                    var barHeight = y(d[1])
                    var xTranslate = i  * barSize,
                        yTranslate = height - barHeight

                    console.log(d[1], total, d[1]/total, barHeight, height)
                    
                    return "translate(" + xTranslate + "," + yTranslate + ")"; 
                })
                .attr("width", function(d) { return barSize ; })
                .attr("height", function(d) { return y(d[1]); })
                .style("fill", "#69b3a2")
                .style('stroke', 'white')
        
            r.exit().remove()

            return
        }

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
            .style("fill", "#69b3a2")
        
        r.exit().remove()
    }
}


export default HistogramPlotter