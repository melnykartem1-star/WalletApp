import { useNavigate, useLocation } from 'react-router-dom';
import { useTranslation } from 'react-i18next';

export default function Sidebar() {
    const navigate = useNavigate();
    const location = useLocation();
    const { t } = useTranslation();

    const menuItems = [
        { path: '/dashboard', label: t('menu.dashboard') },
        { path: '/transactions', label: t('menu.transactions') },
        { path: '/categories', label: t('menu.categories') },
        { path: '/merchants', label: t('menu.merchants') },
        { path: '/profile', label: t('menu.profile') }
    ];

    const handleLogout = () => {
        localStorage.clear();
        navigate('/login');
    };

    return (
        <div style={{ width: '250px', background: '#1e1e1e', height: '100vh', position: 'fixed', borderRight: '1px solid #333', display: 'flex', flexDirection: 'column', padding: '20px', boxSizing: 'border-box', color: '#fff' }}>
            <h2 style={{ textAlign: 'center', marginBottom: '30px', color: '#4CAF50', marginTop: '10px' }}>WalletApp</h2>
            
            <nav style={{ display: 'flex', flexDirection: 'column', gap: '5px', flex: 1 }}>
                {menuItems.map(item => {
                    const isActive = location.pathname.startsWith(item.path);
                    return (
                        <button 
                            key={item.path} 
                            onClick={() => navigate(item.path)}
                            style={{ padding: '12px 15px', textAlign: 'left', background: isActive ? '#333' : 'transparent', color: isActive ? '#fff' : '#aaa', border: 'none', borderRadius: '6px', cursor: 'pointer', fontSize: '16px', transition: '0.2s', fontWeight: isActive ? 'bold' : 'normal' }}
                        >
                            {item.label}
                        </button>
                    );
                })}
            </nav>

            <div style={{ marginTop: 'auto' }}>
                <button onClick={handleLogout} style={{ width: '100%', padding: '12px', background: '#d32f2f', color: '#fff', border: 'none', borderRadius: '6px', cursor: 'pointer', fontWeight: 'bold', transition: '0.2s' }}>
                    {t('menu.logout')}
                </button>
            </div>
        </div>
    );
}