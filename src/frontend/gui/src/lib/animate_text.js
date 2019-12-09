import $ from 'jquery'

function animate_text(text, speed, outputer){
    
    var initialText = text
    
    var render = ""
    var i = 0
        
    var outputInterval = setInterval(function(){
        
        render += initialText[i]
        outputer(render)
        
        
        i++
        if (i >= initialText.length){
            clearInterval(outputInterval)
        }
    }, speed)
            
}

function animate_html_element(element, speed){
    
    var text = $(element).text().trim()
        
    var outputer = function(text){
        $(element).show()
        $(element).text(text)
    }
    
    animate_text(text, speed, outputer)
}


export default animate_html_element