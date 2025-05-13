import { useContext, useEffect, useState } from "react";
import { Button, Col, Container, Form, Nav, Navbar, NavDropdown, Row } from "react-bootstrap";
import { Link, useNavigate } from "react-router-dom";
import Apis, { endpoints } from "../../configs/Apis";

const Header = () => {
    const [categories, setCategories] = useState([]);
   

    const loadCates = async () => {
        let res = await Apis.get(endpoints['categories']);
        setCategories(res.data);
    }
      useEffect(() => {
        loadCates();
    }, []);

    
    return (
    <Navbar expand="lg" className="bg-body-tertiary">
      <Container>
        <Navbar.Brand href="#home">DT's SocialNetwork</Navbar.Brand>
        <Navbar.Toggle aria-controls="basic-navbar-nav" />
        <Navbar.Collapse id="basic-navbar-nav">
          <Nav className="me-auto">
            <Link to="/" className="nav-link">Trang chủ</Link>
            <NavDropdown title="Danh mục" id="basic-nav-dropdown">

              {categories.map(c => <Link key={c.id} to={`/?cateId=${c.id}`} className="dropdown-item">{c.name}</Link> )}

            </NavDropdown>

            
            
          </Nav>
         
        </Navbar.Collapse>
      </Container>
    </Navbar>
    )
}

export default Header;