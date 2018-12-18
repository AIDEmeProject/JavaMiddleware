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
                                this.props.finalGroups.map((g, i) => {
                                    
                                    return (
                                        <th 
                                            key ={i}
                                            colSpan={g.length}
                                        >
                                            {g.map(e => e.name).join(", ")}
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
                                    this.props.finalGroups.map((g, iGroup) => {
                                        
                                        var values = g.map( variable => {
                                            
                                            return point.data[variable.i]

                                        }).join(", ")
                                                                                                                         
                                        if ( typeof point.label !== "undefined"){
                                            var L = () => {
                                                return <button
                                                            data-point={i}
                                                            data-subgroup={iGroup}
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
                                        
                                        return (
                                            <td 
                                                colSpan={g.length}
                                                key={iGroup}
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