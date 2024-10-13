import React from 'react'
import { Routes, Route } from "react-router-dom"
import { Landing, Dashboard } from "../pages"
import { Downloads, SignIn, Downloading } from '../components'

export const AllRoutes = () => {
  return (
    <Routes>
      <Route path="/" element={<Landing />} />
      <Route path="/login" element={<SignIn />} />
      <Route path="/dashboard" element={<Dashboard />} />
      <Route path="/dashboard/downloads" element={<Dashboard ChildComponent={Downloads} />}/>
      <Route path="/dashboard/profile" element={<Dashboard ChildComponent={Downloads} />}/>
    </Routes>
  )
}
