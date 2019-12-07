import React, { Component } from 'react';


class GridOrDatasetOption extends Component{

    render(){
        return (
            <div>
               <select onChange={this.onFakePointOrRealChange}>
                    <option>Fake point</option>
                    <option>Real dataset</option>
                </select>
            </div>
        )
    }
}

export default GridOrDatasetOption