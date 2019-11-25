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

        const negativeSamples = labeledPoints.filter(e => e.label === 0)
        const positiveSamples = labeledPoints.filter(e => e.label === 1)

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
                        labeledPoints={this.props.labeledPoints}
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

                <div className="col col-lg-8">
                    <svg id="scatterplot-svg"></svg>                
                </div>
        
            </div>
        )
    }

    componentDidMount(){
        
        this.plotter = new ModelBehaviorPlotter()
        this.plotter.createPlot("#scatterplot-svg", this.state.scale)
        this.plotter.plotData(
            this.state.scale,              
            this.getLabeledEmbedding(),
            this.getGridPoints(), 
            this.getChosenVariables()
        )
        
    }

    getLabeledPoints(){

        const iteration = this.state.modelIteration

        const labeledPoints = this.props.labeledPoints.filter((e, i) =>{
            return i <= iteration
        })
        
        return labeledPoints
    }
    getEmbbedings(){
        return this.getGridPoints()
    }

    getLabeledEmbedding(){
        const labeledPoints = this.getLabeledPoints()

        const embeddings = this.getEmbbedings()

        const labeledEmbeddings =  labeledPoints.map( e => {
            
            return embeddings[e.id]
        })
        
        return labeledEmbeddings
    }

    componentDidUpdate(){
        //this.plotter.updatePlot(this.getChosenVariables(), this.state.scale)
        const embbedings = this.getGridPoints()

        const scale = this.computeMinAndMaxScale(embbedings.map(e => e[0]), embbedings.map(e => e[1]))
        this.plotter.plotData(
            scale,              
            this.getLabeledEmbedding(),
            embbedings, 
            this.getChosenVariables()
        )
    }

    onPreviousIteration(){

        var iteration = this.state.modelIteration - 1
        this.setState({
            modelIteration: Math.max(iteration, 0)
        })
        
    }

    onNextIteration(){

        const history = this.props.history
        var iteration = this.state.modelIteration + 1

        this.setState({
            modelIteration: Math.min(iteration, history.length - 1)
        })        
    }

    getGridPoints(){
        
        const iterationData = this.props.history[this.state.modelIteration]
        console.log(iterationData)
        return iterationData.embedding       
    }


    componentWillReceiveProps(nextProps){   
        
        const labeledPoints = nextProps.labeledPoints
        
        if (labeledPoints.length > 3){

            //this.getGridPoints()
            //this.removeData(this.svg, labeledPoints, this.props.gridPoints)
            this.plotter.plotData(this.svg, 
                this.state.scale, 
                this.width, 
                this.height, 
                labeledPoints, 
                this.getGridPoints(), 
                this.getChosenVariables())
        }
    }

    computeMinMaxOfRawData(iFirstVariable, iSecondVariable){


        const realIdOne = this.props.availableVariables[iFirstVariable].idx
        const realIdTwo = this.props.availableVariables[iSecondVariable].idx
        
        const datasetInfos = this.props.datasetInfos
                
        return this.computeMinAndMaxScale(datasetInfos[realIdOne], datasetInfos[realIdTwo])
    }

    computeMinAndMaxScale(xValues, yValues){

        var scale = {
            xMin: d3.min(xValues),
            xMax: d3.max(xValues),
            yMin: d3.min(yValues),
            yMax: d3.max(yValues)
        }
        
        return scale
    }

    getChosenVariables(){
        const variables = [this.state.firstVariable, this.state.secondVariable]
        
        return variables
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
    
}

var xMin = 0, 
    xMax = 20,
    yMin = 0,
    yMax = 20

var nPoints = 80

var xRange = d3.range(xMin, xMax, (xMax - xMin) / nPoints),
    yRange = d3.range(xMin, yMax, (yMax - yMin) / nPoints)

const fake1 = generateFakePoints(xRange, yRange)
const fake2 = generateFakePoints(xRange, yRange)



const d = {
    
    "embedding": [
    [
        6.6314544677734375, 
        1.7069072723388672, 
        1.0
    ], 
    [
        4.779907703399658, 
        4.3746232986450195, 
        1.0
    ], 
    [
        -0.5685606002807617, 
        11.141488075256348, 
        1.0
    ], 
    [
        -0.16208447515964508, 
        11.511297225952148, 
        1.0
    ], 
    [
        -7.838195323944092, 
        0.18343234062194824, 
        1.0
    ], 
    [
        -9.697022438049316, 
        -6.937088489532471, 
        0.0
    ], 
    [
        -0.8049123287200928, 
        10.952290534973145, 
        1.0
    ], 
    [
        5.085594654083252, 
        4.069980144500732, 
        1.0
    ], 
    [
        1.8216571807861328, 
        11.191843032836914, 
        1.0
    ], 
    [
        0.11190945655107498, 
        11.648695945739746, 
        0.0
    ], 
    [
        -9.648449897766113, 
        -8.574637413024902, 
        1.0
    ], 
    [
        -0.8103950619697571, 
        10.9370698928833, 
        0.0
    ], 
    
    [
        -8.879761695861816, 
        1.3719234466552734, 
        0.0
    ], 
    [
        -9.38370418548584, 
        -7.433465003967285, 
        1.0
    ], 
    [
        10.157100677490234, 
        -5.933054447174072, 
        0.0
    ], 
    [
        8.675350189208984, 
        -1.7537881135940552, 
        1.0
    ], 
    [
        8.412959098815918, 
        -1.461712121963501, 
        0.0
    ], 
    [
        1.5539385080337524, 
        11.286014556884766, 
        0.0
    ], 
    [
        6.238748550415039, 
        2.75685715675354, 
        1.0
    ], 
    [
        -9.743083953857422, 
        -5.694484233856201, 
        0.0
    ], 
    [
        6.810112953186035, 
        1.3901573419570923, 
        0.0
    ], 
    [
        6.983276844024658, 
        1.1797120571136475, 
        0.0
    ], 
    [
        -0.785017728805542, 
        10.98387336730957, 
        1.0
    ], 
    [
        8.14511489868164, 
        -0.8582469820976257, 
        1.0
    ], 
    [
        10.184682846069336, 
        -5.946341037750244, 
        1.0
    ], 
    [
        2.78277850151062, 
        11.339861869812012, 
        1.0
    ], 
    [
        9.710125923156738, 
        -3.4237120151519775, 
        1.0
    ], 
    [
        -8.447199821472168, 
        4.427943706512451, 
        1.0
    ], 
    [
        -9.204044342041016, 
        2.181438684463501, 
        1.0
    ], 
    [
        -8.607685089111328, 
        -4.163544178009033, 
        1.0
    ], 
    [
        -8.04598617553711, 
        -2.2373642921447754, 
        1.0
    ], 
    [
        2.7975804805755615, 
        11.075854301452637, 
        1.0
    ], 
    [
        10.240021705627441, 
        -5.848012447357178, 
        1.0
    ], 
    [
        -8.907685279846191, 
        3.7134904861450195, 
        1.0
    ], 
    [
        6.659791946411133, 
        2.1227095127105713, 
        1.0
    ], 
    [
        -7.48036527633667, 
        -0.141850084066391, 
        1.0
    ], 
    [
        -9.382011413574219, 
        -7.396576404571533, 
        1.0
    ], 
    [
        -8.843470573425293, 
        -4.253382205963135, 
        1.0
    ], 
    [
        5.354788303375244, 
        3.639265298843384, 
        0.0
    ], 
    [
        -7.875974178314209, 
        -1.710100769996643, 
        1.0
    ], 
    [
        7.014988899230957, 
        1.6969362497329712, 
        1.0
    ], 
    [
        0.4509063959121704, 
        11.365342140197754, 
        1.0
    ], 
    [
        4.845384120941162, 
        4.326573848724365, 
        1.0
    ], 
    [
        9.955839157104492, 
        -3.9620838165283203, 
        1.0
    ], 
    [
        8.393305778503418, 
        -1.3593502044677734, 
        1.0
    ], 
    [
        2.6502766609191895, 
        10.988636016845703, 
        1.0
    ], 
    [
        -8.460102081298828, 
        4.9286675453186035, 
        1.0
    ], 
    [
        -9.517230033874512, 
        -7.218629837036133, 
        1.0
    ], 
    [
        -0.6420661211013794, 
        11.227463722229004, 
        1.0
    ], 
    [
        -0.06403443962335587, 
        11.572739601135254, 
        1.0
    ], 
    [
        5.151364326477051, 
        3.9874231815338135, 
        1.0
    ], 
    [
        10.2652587890625, 
        -4.7694878578186035, 
        1.0
    ], 
    [
        -8.262139320373535, 
        -2.738879919052124, 
        1.0
    ], 
    [
        10.315261840820312, 
        -4.9285783767700195, 
        1.0
    ], 
    [
        -8.60484504699707, 
        1.0225255489349365, 
        1.0
    ], 
    [
        4.817389488220215, 
        4.166478157043457, 
        1.0
    ], 
    [
        2.3115482330322266, 
        11.200616836547852, 
        1.0
    ], 
    [
        10.265813827514648, 
        -5.351025581359863, 
        1.0
    ], 
    [
        -7.729633331298828, 
        0.05888275429606438, 
        1.0
    ], 
    [
        -9.419160842895508, 
        -7.992431640625, 
        1.0
    ], 
    [
        6.6542582511901855, 
        1.9308836460113525, 
        1.0
    ],     
]


}

const history = [    
    d
]


function generateFakePoints(xRange, yRange){

    var gridPoints = []
    xRange.forEach(x => {

        yRange.forEach(y => {   
        
            {                
                gridPoints.push({
                    data: [x, y, x - y],
                    label:  Math.floor(Math.random()* 2)
                })            
            }
        })
    })
    return gridPoints
}

ModelBehavior.defaultProps = {

    labeledPoints: [
        {
            'id': 1,
            'label': 1,
            'data': [0, 0, 1, 0]
        },
        {
            'id': 2,
            'label': 1,
            'data': [0, 1, 2, 3]
        },
        {
            'id': 3,
            'label': 0,
            'data': [1, 0, 3, 2]
        },
        {
            'id': 4,
            'label': 1,
            'data': [1, 1, 4, 1]
        },
    ],
    
    datasetInfos: {
        minimums: [0, 0, 0],
        maximums: [10, 10, 10]
    },

    history: history,
    iteration: 0,
    availableVariables: [
        {
            'name': 'test1',
            'realId': 0,
            'idx': 0,
            'id': 1
        },
        {
            'name': 'test2',
            'realId': 1,
            'idx': 0,
            'id': 2
        },  
        {
            'name': 'test3',
            'realId': 2,
            'idx': 0,
            'id': 3
        },
    ]
}

export default ModelBehavior