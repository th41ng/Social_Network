// Import các chức năng cần thiết từ Firebase SDK
import { initializeApp } from "firebase/app";
import { getDatabase } from "firebase/database";

// Cấu hình Firebase cho ứng dụng của bạn
const firebaseConfig = {
  apiKey: "AIzaSyClJzqT_U1bZOVMQPxz5igcziWM4S0_bPo",
  authDomain: "socialnetwork-852e0.firebaseapp.com",
  databaseURL: "https://socialnetwork-852e0-default-rtdb.asia-southeast1.firebasedatabase.app",
  projectId: "socialnetwork-852e0",
  storageBucket: "socialnetwork-852e0.appspot.com",
  messagingSenderId: "217929366385",
  appId: "1:217929366385:web:c4d63d146c454bb0ca54d0",
  measurementId: "G-BEDGMX3BTT",
};

// Khởi tạo Firebase App
const app = initializeApp(firebaseConfig);

// Khởi tạo Realtime Database
const db = getDatabase(app);

// Xuất `db` để sử dụng ở nơi khác
export { db };
