import React, { Component } from 'react';

class TSMExploration extends Component{

    constructor(props){
        
        super(props)

        this.state = {
            pointsToLabel: this.props.pointsToLabel,
            noPoints: []
        }        
    }

    render(){

        return (
            <div>

                <p>
                    Grouped variable exploration. If you chose no, you will be asked to label each subgroups
                    independantly                    
                </p>

                <table className="group-variable">
                    <thead>
                        <tr>
                            {
                                this.props.variableGroups.map((g, i) => {
                                    
                                    var reducer = (acc, v) => { return v. name + ', ' + acc }
                                    var names = g.reduce(reducer, "")
                                    names = names.slice(0, -2)
                                    return (
                                        <th 
                                            key ={i}
                                            colSpan={g.length}
                                        >
                                            {names}
                                        </th>
                                    )
                                })
                            }
                            <th>
                                Label    
                            </th>                
                        </tr>
                    </thead>

                    <tbody>                
                    {
                    this.state.pointsToLabel.map((point, i) => {

                        return (

                            <tr 
                                key={i}
                                className="variable-group">
                                {
                                    this.props.variableGroups.map((g, j) => {
                                        
                                        var values = g.reduce((acc, v) => {
                                            
                                            return point.data[v.id] + ", " + acc

                                        }, "")
                                        
                                        
                                        if ( typeof point.label !== "undefined"){
                                            var L = () => {
                                                return <button
                                                            data-point={i}
                                                            data-subgroup={j}
                                                            className="btn btn-primary btn-raised"
                                                            onClick = {this.onSubGroupNo.bind(this)}
                                                        >
                                                            No
                                                        </button>
                                            }
                                        }
                                        else{
                                            var L = () => {return <span></span>}
                                        }
                                         
                                        values = values.slice(0, -2)
                                        
                                        return (
                                            <td 
                                                colSpan={g.length}
                                                key={j}
                                            >
                                                {values} <L />
                                            </td>
                                        )
                                    })
                                }
                                <td>
                                
                                    <button 
                                        style={{display: typeof point.label === "undefined" ? "inherit": "none"}}
                                        className="btn btn-primary btn-raised"
                                        data-point={i}
                                        onClick={this.groupWasLabeledAsYes.bind(this)}
                                    >
                                        Yes
                                    </button>
                                                        
                                    <button 
                                        style={{display: typeof point.label === "undefined" ? "inherit": "none"}}
                                        className="btn btn-primary btn-raised"
                                        data-point={i}
                                        onClick={this.groupWasLabeledAsNo.bind(this)}
                                    >
                                        No
                                    </button>

                                    <button
                                        className="btn btn-primary btn-raised"
                                        style={{display: typeof point.label === "undefined" ? "none": "inherit"}}
                                        data-point={i}
                                        onClick={this.groupSubLabelisationFinished.bind(this)}
                                    >
                                        Validate Subgroup labels
                                    </button>
                                </td>
                            </tr>
                        )
                    })
                }
                </tbody>

                </table>
            </div>
        )
    }


    groupSubLabelisationFinished(e){
        
        var iPoint = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        var point = pointsToLabel[iPoint]

        if ( typeof point.subGroupLabels === "undefined"){
            alert('please label at least one subgroup')
            return
        }

        pointsToLabel.splice(iPoint, 1)

        this.setState({
            pointsToLabel: pointsToLabel
        })
    }

    onSubGroupNo(e){

        var data = e.target.dataset
        var iPoint = data.point
        var iSubgroup = data.subgroup

        var pointsToLabel = this.state.pointsToLabel.map(e => e)
        var point = pointsToLabel[iPoint]

        var label = {
            iSubgroup: iSubgroup,
            label: 0
        }

        if ( ! point.subGroupLabels){
            point.subGroupLabels = [
                label
            ]
        }
        else{
            point.subGroupLabels.push(label)
        }

        this.setState({
            pointsToLabel: pointsToLabel
        })

    }

    groupWasLabeledAsYes(e){

        var pointsToLabel = this.state.pointsToLabel.map(e => e)


        pointsToLabel.splice(e.target.dataset.point, 1)

        this.setState({
            pointsToLabel: pointsToLabel
        })
      
    }

    groupWasLabeledAsNo(e){

        var iPoint = e.target.dataset.point
        var pointsToLabel = this.state.pointsToLabel.map(e => e)


        var point = pointsToLabel[iPoint]
        point.label = 0

        this.setState({
            pointsToLabel: pointsToLabel
        })   
    }
}

TSMExploration.defaultProps = {

    variableGroups: [
        [
            {name: "Age", id: 2},            
        ],
        [
            {name: "City", id: 1},
            {name: "Social Category", id: 0},
        ],
        [
            {name: "Sex", id: 3},
        ]
    ],

    pointsToLabel:[

        {
            data: [0, 0 , 22, 0]
        },

        {
            data: [0, 1, 33, 1]
        },
    ]
}

export default TSMExploration