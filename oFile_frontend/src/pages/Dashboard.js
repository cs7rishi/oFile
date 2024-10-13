import { Sidebar } from "../components"
import "../css/dashboard.css"
export const Dashboard = ({ ChildComponent }) => {
  return (
    <>
      <div className="flex">
        <div className="sidebar grow-0">
          <Sidebar />
        </div>
        <div className="dashboard-content grow px-20 pt-5">
          {ChildComponent && <ChildComponent />}
        </div>
      </div>
    </>
  )
}
