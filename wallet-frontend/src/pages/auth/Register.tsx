import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { axiosInstance } from '../../api/axiosInstance';

const Register: React.FC = () => {
  const { t } = useTranslation();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      await axiosInstance.post('/auth/register', { username, email, password });
      alert(t('auth.successRegister'));
      navigate('/login');
    } catch (error) {
      alert(t('auth.errorRegister'));
    }
  };

  return (
    <div style={{ maxWidth: '400px', margin: '100px auto', padding: '30px', border: '1px solid #333', borderRadius: '8px', backgroundColor: '#1e1e1e', color: '#fff', fontFamily: 'sans-serif' }}>
      <h2 style={{ textAlign: 'center', marginBottom: '24px' }}>{t('auth.registerTitle')}</h2>
      <form onSubmit={handleSubmit}>
        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px', color: '#aaa' }}>{t('auth.username')}</label>
          <input
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            required
            style={{ width: '100%', padding: '10px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#2a2a2a', color: '#fff' }}
          />
        </div>
        <div style={{ marginBottom: '16px' }}>
          <label style={{ display: 'block', marginBottom: '6px', color: '#aaa' }}>{t('auth.email')}</label>
          <input
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            style={{ width: '100%', padding: '10px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#2a2a2a', color: '#fff' }}
          />
        </div>
        <div style={{ marginBottom: '24px' }}>
          <label style={{ display: 'block', marginBottom: '6px', color: '#aaa' }}>{t('auth.password')}</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            style={{ width: '100%', padding: '10px', boxSizing: 'border-box', borderRadius: '4px', border: '1px solid #444', backgroundColor: '#2a2a2a', color: '#fff' }}
          />
        </div>
        <button type="submit" style={{ width: '100%', padding: '12px', background: '#4caf50', color: '#fff', border: 'none', borderRadius: '4px', cursor: 'pointer', fontSize: '16px', fontWeight: 'bold' }}>
          {t('auth.signUpBtn')}
        </button>
      </form>
      <p style={{ marginTop: '20px', textAlign: 'center', color: '#aaa' }}>
        {t('auth.hasAccount')}{' '}
        <Link to="/login" style={{ color: '#4caf50', textDecoration: 'none' }}>
          {t('auth.signInBtn')}
        </Link>
      </p>
    </div>
  );
};

export default Register;