import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layouts/Header";
import Footer from "./components/layouts/Footer";
import Home from "./components/Home";
import Register from "./components/Register";
import Login from "./components/Login";
import Notifications from "./components/Notifications";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import React, { useReducer } from 'react';
import MyUserReducer from './reducer/MyUserReducer'; 
import { MyUserContext, MyDispatchContext } from './configs/Contexts';
import ResetPassword from "./components/ResetPassword";
import Profile from "./components/Profile";


const App = () => {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatchContext.Provider value={dispatch}>
        <BrowserRouter>

          <Header />

          <Container>
            <Routes>
              <Route path="/" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/home" element={<Home />} />
              <Route path="/notifications" element={<Notifications />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="/profile" element={<Profile />} />
            </Routes>
          </Container>

          <Footer />
        </BrowserRouter>
      </MyDispatchContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;