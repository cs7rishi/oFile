import React from 'react'
import { Routes, Route } from "react-router-dom"
import { Landing, Dashboard } from "../pages"
import { Downloads, SignIn, CommingSoon } from '../components'

export const AllRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<SignIn />} />
      <Route path="/dashboard" element={<Dashboard ChildComponent={Downloads} />}/>
      {/* <Route path="/dashboard/downloads" element={<Dashboard ChildComponent={Downloads} />}/> */}
      {/* <Route path="/dashboard/upgrade" element={<Dashboard ChildComponent={CommingSoon} />}/> */}
      {/* <Route path="/dashboard/profile" element={<Dashboard ChildComponent={CommingSoon} />}/> */}
      {/* <Route path="/dashboard/setting" element={<Dashboard ChildComponent={CommingSoon} />}/> */}
    </Routes>
  )
}
