
import { BrowserRouter, Route, Routes } from "react-router-dom";
import Header from "./components/layouts/Header";
import Footer from "./components/layouts/Footer";
import Home from "./components/Home";
import Register from "./components/Register";
import Login from "./components/Login";
import Chats from "./components/Chats";
import Notifications from "./components/Notifications";
import 'bootstrap/dist/css/bootstrap.min.css';
import { Container } from "react-bootstrap";
import React, { useReducer, useEffect } from 'react';
import MyUserReducer from './reducer/MyUserReducer';
import { MyUserContext, MyDispatchContext } from './configs/Contexts';
import ResetPassword from "./components/ResetPassword";
import Profile from "./components/Profile";
import SurveyListPage from './components/surveys/SurveyListPage'; 
import TakeSurveyPage from './components/surveys/TakeSurveyPage'; 
import cookie from 'react-cookies'; 
import { authApis, endpoints } from './configs/Apis'; 

const App = () => {
  const [user, dispatch] = useReducer(MyUserReducer, null);

  useEffect(() => {
    const fetchCurrentUserOnLoad = async () => {
      const token = cookie.load('token');
     
      if (token && !user) {
        try {
          console.log("App.js: Attempting to fetch current user with token...");
          const res = await authApis().get(endpoints['profile']);
          dispatch({ type: 'login', payload: res.data });
          console.log("App.js: Current user fetched and dispatched:", res.data);
        } catch (ex) {
          console.error("App.js: Error auto-logging in:", ex);
          if (ex.response && ex.response.status === 401) {
           
            cookie.remove('token'); 
            dispatch({ type: 'logout' }); 
          
          }
        }
      }
    };

    fetchCurrentUserOnLoad();
  }, [dispatch, user]); 

  return (
    <MyUserContext.Provider value={user}>
      <MyDispatchContext.Provider value={dispatch}>
        <BrowserRouter>
          <Header />
          <Container>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/login" element={<Login />} />
              <Route path="/register" element={<Register />} />
              <Route path="/home" element={<Home />} />
              <Route path="/notifications" element={<Notifications />} />
              <Route path="/surveys" element={<SurveyListPage />} />
              <Route path="/surveys/:surveyId/take" element={<TakeSurveyPage />} />
              <Route path="/profile" element={<Profile />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route path="/chats" element={<Chats/>} />
            </Routes>
          </Container>
          <Footer />
        </BrowserRouter>
      </MyDispatchContext.Provider>
    </MyUserContext.Provider>
  );
}

export default App;