import React, { Component } from 'react';
import sendFilters from '../../../actions/sendFilters';
import PointLabelisation from '../../PointLabelisation';


class NumericalFilter extends Component{

    render(){

        const variable = this.props.variable
        const min = this.props.min,
              max = this.props.max

        return (
            <div className="card filter inline-block">

                    <p>
                        {variable.name} <br />
                        range: [{this.state.minValue}, {this.state.maxValue}]
                    </p>
                    <label>
                        Min                        
                        <input 
                            value={this.state.minValue}
                            onChange={this.minChanged.bind(this)}
                            type="range"
                            min={-1000}
                            max={100000}
                        />
                    </label>

                    <br />
                    
                    <label>
                        Max: {this.state.max}                
                        <input 
                            value={this.state.maxValue}
                            onChange={this.maxChanged.bind(this)}
                            type="range"
                            min={-1000}
                            max={100000}
                        />
                    </label>
            </div>
        )        
    }

    constructor(props){
        super(props)
        this.state = {
            
            minValue: 0,
            maxValue: 100000
        }
    }

    minChanged(e){
        var newValue = parseFloat(e.target.value)
        
        if (newValue >= this.state.maxValue){
            newValue = this.state.maxValue
        }
        this.setState({
            minValue:newValue
        })

        const iFilter = this.props.iFilter
        this.props.filterChanged(iFilter, {min: newValue})
    }

    maxChanged(e){
        var newValue = parseFloat(e.target.value)
        if (newValue <= this.state.minValue){
            newValue = this.state.minValue
        }
        
        this.setState({maxValue: newValue})

        const iFilter = this.props.iFilter
        this.props.filterChanged(iFilter, {max: newValue})
    }
}

class CategoricalFilter extends Component{

    render(){

        return (
            <div className="card filter categorical-filter inline-block">

                <p>{this.props.variable.name}</p>
                {
                    this.props.variable.values.map((value, i) => {

                        return (
                            <div>
                                <label htmlFor={"cat-filter-" + i}>
                                    {value}
                                </label>
                                <input 
                                    type="checkbox"
                                    data-value={value} 
                                    onChange={this.categoryWasClicked.bind(this)}    
                                />
                            </div>
                        )
                    })
                }
            </div>
        )
    }

    constructor(props){
        super(props)
        this.state = {
            filterValues: []
        }
    }

    categoryWasClicked(e){

        const iFilter = this.props.iFilter
        var isIncluded = e.target.checked
        var value = e.target.dataset.value

        if (isIncluded){
            var filterValues = this.state.filterValues
            filterValues.push(value)
        }
        else{
            var filterValues = this.state.filterValues.filter( e => e !== value)
        }
        
        this.setState({
            filterValues: filterValues
        }, () => {
            this.props.filterChanged(iFilter, {filterValues: filterValues})
        })
        
    }
}

class PointFiltering extends Component{

    render(){
        return (
            <div>
                {
                    this.props.chosenVariables.map( (variable, i) => {

                        var Filter = variable.type === "numerical" ? NumericalFilter: CategoricalFilter
                        return (
                            
                            <Filter          
                                iFilter={i}   
                                filterChanged={this.filterChanged.bind(this)}
                                key={i}
                                variable={variable}                                
                            />                            
                        )
                    })
                }

                <p>                
                    <button
                        className="btn btn-raised"
                        onClick={this.getPoints.bind(this)}
                    >
                        Get Points
                    </button>
                </p>


                <div>

                    <PointLabelisation
                        chosenColumns={this.props.chosenVariables}
                        dataset={this.props.dataset}
                        pointsToLabel={this.state.points}
                        onPositiveLabel={this.onPositiveLabel.bind(this)}
                        onNegativeLabel={this.onNegativeLabel.bind(this)}
                    />
                </div>
            </div>
        )
    }

    constructor(props){

        super(props)
        this.state = {
            filters: this.props.chosenVariables.map( e => { 
                return {
                    'columnName': e.name,                
                }
            }),
            points: []
        }        
    }

    onPositiveLabel(e){
        var iPoint = e.target.dataset.key

        var points = this.state.points
        points.splice(iPoint, 1)
        this.setState({
            points
        })

        this.props.onPositiveLabel(e)
    }

    onNegativeLabel(e){

        var iPoint = e.target.dataset.key

        var points = this.state.points
        points.splice(iPoint, 1)
        this.setState({
            points
        })
        this.props.onNegativeLabel(e)
    }

    filterChanged(iFilter, change){
        var filter = this.getFilter(iFilter)
        
        filter = Object.assign(filter, change)
        
        var filters = this.updateFilter(iFilter, filter)
        
        this.setState({
            filters: filters
        })
    }

    updateFilter(iFilter, filter){
        var filters = this.state.filters.map(e => e)
        filters[iFilter] = filter
        return filters
    }

    getFilter(iFilter){
        return this.state.filters[iFilter]
    }

    getPoints(){
        
        const filters = this.state.filters
        
        sendFilters(filters, this.pointsReceived.bind(this))
    }

    pointsReceived(points){

        var receivedPoints = points.map(e => {
            return {
                id: e.id,
                data: e.data.array
            }
        }).filter((e, i) => { return i < 25})
        this.setState({
            points: receivedPoints
        })
    }

}


export default PointFiltering