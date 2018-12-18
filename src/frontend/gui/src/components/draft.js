
class PointsAsTable extends Component{

    render(){
        return (
            <table>
                    <thead>
                            <tr>
                                {
                                    this.props.columns.map((col, k) => {
                                    
                                        return (
                                            <th key={k}>
                                                { col }
                                            </th>
                                        )
                                    })
                                }
                            </tr>
                        </thead>
                    <tbody>
                        {
                            this.props.rows.map((row, k) => {
                                return (

                                    <tr>
                                        <td>
                                            {row}
                                        </td>
                                    </tr>
                                )
                            })
                        }
                    </tbody>
            </table>
        )
    }
}