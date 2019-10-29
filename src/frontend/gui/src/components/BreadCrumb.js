import React, { Component } from 'react';

class BreadCrumb extends Component{

  render(){

        return (
                                  
            <ol className="breadcrumb">
                
                {
                    this.props.items.map((item, i) => {

                        const className = item.active ? "breadcrumb-item active": "breadcrumb-item"
                        return (
                            <li key={i} className={className} aria-current="page">{item.name}</li>
                        )
                    })
                }                        
            </ol>
                    
        )
    }
}


export default BreadCrumb