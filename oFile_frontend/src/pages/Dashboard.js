import { Sidebar } from "../components"
import "../css/dashboard.css"
export const Dashboard = ({ ChildComponent }) => {
  return (
    <>
      <div className="dashboard-layout">
        <div className="sidebar">
          <Sidebar />
        </div>
        <div className="dashboard-content">
          {ChildComponent && <ChildComponent />}
        </div>
      </div>
    </>
  )
}
