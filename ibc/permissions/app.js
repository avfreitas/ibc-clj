document.addEventListener('DOMContentLoaded', function () {
    const listPermissionsButton = document.getElementById('list-permissions');
    const permissionsList = document.getElementById('permissions-list');
    const addPermissionForm = document.getElementById('add-permission-form');
    const deletePermissionForm = document.getElementById('delete-permission-form');
    const checkPermissionIdForm = document.getElementById('check-permission-id-form');
    const updatePermissionForm = document.getElementById('update-permission-form');
    
    const backendUrl = 'https://www.dev-app.qualitsys.net/ibc-clj';  
    
    async function listPermissions() {
        try {
            const response = await fetch(`${backendUrl}/list-permissions`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`Erro ao listar permissions: ${response.statusText}`);
            }
            const data = await response.json();
            permissionsList.innerHTML = '';
            data.forEach(permission => {
                const li = document.createElement('li');
                li.textContent = `${permission['permissions/id']}: ${permission['permissions/description']}`;
                permissionsList.appendChild(li);
            });
        } catch (error) {
            console.error('Erro ao listar permissions:', error);
            alert(`Erro ao listar permissions: ${error.message}`);
        }
    }

    async function checkPermissionExists(id) {
        try {
            const response = await fetch(`${backendUrl}/list-permissions`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`Erro em fetch de permissions: ${response.statusText}`);
            }
            const data = await response.json();
            return data.find(permission => permission['permissions/id'] === parseInt(id));
        } catch (error) {
            console.error('Erro em fetch de permissions:', error);
            return null;
        }
    }

    async function addPermission(event) {
        event.preventDefault();
        try {
            const formData = new FormData(addPermissionForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            if (!data.id || !data.description) {
                alert('Por favor, preencha todos os campos obrigatórios do formulário.');
                return;
            }

            const jsonData = JSON.stringify(data);

            const response = await fetch(`${backendUrl}/ins-permission`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: jsonData
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao adicionar permission: ${response.statusText}, ${errorText}`);
            }

            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao adicionar permission:', error);
            alert(`Erro ao adicionar permission: ${error.message}`);
        }
    }

    async function deletePermission(event) {
        event.preventDefault();
        try {
            const formData = new FormData(deletePermissionForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            const response = await fetch(`${backendUrl}/del-permission`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao deletar permission: ${response.statusText}, ${errorText}`);
            }
            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao deletar permission:', error);
            alert(`Erro ao deletar permission: ${error.message}`);
        }
    }

    function showUpdatePermissionFields(permission) {
        document.getElementById('update-permission-id').value = permission['permissions/id'];
        document.getElementById('update-permission-description').value = permission['permissions/description'];
        document.getElementById('update-permission-updated_by').value = permission['permissions/updated_by'];
        updatePermissionForm.style.display = 'block';
    }

    async function checkPermissionId(event) {
        event.preventDefault();
        const id = document.getElementById('check-permission-id').value.trim();
        if (!id) {
            alert('Por favor, insira o ID.');
            return;
        }
        const permission = await checkPermissionExists(id);
        if (!permission) {
            alert(`Permission com ID ${id} não encontrado.`);
            return;
        }
        showUpdatePermissionFields(permission);
    }

    async function updatePermission(event) {
        console.log("Função updatePermission em execução!");
        event.preventDefault();
        try {
            const formData = new FormData(updatePermissionForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            console.log('Dados a serem enviados ao backend:', data);

            const response = await fetch(`${backendUrl}/upd-permission`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            console.log('Resposta da solicitação:', response);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to update permission: ${response.statusText}, ${errorText}`);
            }

            const result = await response.json();
            console.log('Resultado da atualização:', result);
            alert(result.message || result.error);
        } catch (error) {
            console.error('Error updating permission:', error);
            alert(`Error updating permission: ${error.message}`);
        }
    }

    listPermissionsButton.addEventListener('click', listPermissions);
    addPermissionForm.addEventListener('submit', addPermission);
    deletePermissionForm.addEventListener('submit', deletePermission);
    checkPermissionIdForm.addEventListener('submit', checkPermissionId);
    updatePermissionForm.addEventListener('submit', updatePermission);
});
