import Sidebar from './Sidebar';

export default function Layout({ children }: { children: React.ReactNode }) {
    return (
        <div style={{ display: 'flex', minHeight: '100vh', background: '#121212' }}>
            <Sidebar />
            <div style={{ marginLeft: '250px', flex: 1, padding: '30px', overflowY: 'auto' }}>
                {children}
            </div>
        </div>
    );
}