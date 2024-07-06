document.addEventListener('DOMContentLoaded', function () {
    const listContactTypesButton = document.getElementById('list-contact-types');
    const contactTypesList = document.getElementById('contact-types-list');
    const addContactTypeForm = document.getElementById('add-contact-type-form');
    const deleteContactTypeForm = document.getElementById('delete-contact-type-form');
    const checkIdForm = document.getElementById('check-id-form');
    const updateContactTypeForm = document.getElementById('update-contact-type-form');
    //const backendUrl = 'http://localhost:8888/ibc-clj';  
    const backendUrl = 'https://www.dev-app.qualitsys.net/ibc-clj';  

    //------------------------------------------------------------------
    // Função para listar os tipos de contato
    //------------------------------------------------------------------
    async function listContactTypes() {
         
        try {
            const response = await fetch(`${backendUrl}/list-contact-types`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`listContactTypes - Erro ao listar contact types: ${response.statusText}`);
            }
            const data = await response.json();
            contactTypesList.innerHTML = '';
            data.forEach(contactType => {
                const li = document.createElement('li');
                li.textContent = `${contactType['contact_types/id']}: ${contactType['contact_types/description']}`;
                contactTypesList.appendChild(li);
            });
        } catch (error) {
            console.error('Erro ao listar contact types:', error);
            alert(`listContactTypes - Erro ao listar contact types: ${error.message}`);
        }
    }

    //---------------------------------------------------------------------------
    // Função para verificar se o contact type existe e obter seus dados
    //---------------------------------------------------------------------------
    async function checkContactTypeExists(id) {
        try {
            const response = await fetch(`${backendUrl}/list-contact-types`, {
                method: 'GET'
            });
            if (!response.ok) {
                throw new Error(`checkContactTypeExists - Erro em fetch de contact types: ${response.statusText}`);
            }
            const data = await response.json();
            return data.find(contactType => contactType['contact_types/id'] === parseInt(id));
        } catch (error) {
            console.error('checkContactTypeExists - Erro em fetch de contact types:', error);
            return null;
        }
    }

    //--------------------------------------------------------------------------------
    // Função para adicionar um novo tipo de contato
    //--------------------------------------------------------------------------------
    async function addContactType(event) {
        
        event.preventDefault();
        try {
            const formData = new FormData(addContactTypeForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            // Log dos valores antes da validação
            console.log('id:', data.id);
            console.log('description:', data.description);
            console.log('priority:', data.priority);
            console.log('status:', data.status);

            // Validação dos campos obrigatórios
            if (!data.id || !data.description || !data.priority || !data.status) {
                alert('Por favor, preencha todos os campos obrigatórios do formulário.');
                return;
            }

            const jsonData = JSON.stringify(data);
            console.log('Dados convertidos para JSON:', jsonData);

            const response = await fetch(`${backendUrl}/ins-contact-type`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: jsonData
            });

            console.log('Resposta da solicitação:', response);
            console.log('Response Headers:', [...response.headers.entries()]); // Log dos cabeçalhos da resposta

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao adicionar contact type: ${response.statusText}, ${errorText}`);
            }

            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao adicionar contact type:', error);
            alert(`Erro ao adicionar contact type: ${error.message}`);
        }
    }

    //-------------------------------------------------------------------------
    // Função para deletar um tipo de contato
    //-------------------------------------------------------------------------
    async function deleteContactType(event) {
        event.preventDefault();
        try {
            const formData = new FormData(deleteContactTypeForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            console.log('Dados a serem enviados ao Backend:', data);

            const response = await fetch(`${backendUrl}/del-contact-type`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            console.log('Resposta da solicitação:', response);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Erro ao deletar contact type: ${response.statusText}, ${errorText}`);
            }
            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Erro ao deletar contact type:', error);
            alert(`Erro ao deletar contact type: ${error.message}`);
        }
    }

    //-------------------------------------------------------------------------------------
    // Função para mostrar os campos de atualização com dados preenchidos
    //-------------------------------------------------------------------------------------
    function showUpdateFields(contactType) {
        document.getElementById('update-id').value = contactType['contact_types/id'];
        document.getElementById('update-description').value = contactType['contact_types/description'];
        document.getElementById('update-priority').value = contactType['contact_types/priority'];
        document.getElementById('update-status').value = contactType['contact_types/status'];
        document.getElementById('update-updated_by').value = contactType['contact_types/updated_by'];
        updateContactTypeForm.style.display = 'block';
    }

    //--------------------------------------------------------------------------------------
    // Função para checar se o ID existe e mostrar os campos de atualização
    //--------------------------------------------------------------------------------------
    async function checkId(event) {
        
        event.preventDefault();
        const id = document.getElementById('check-id').value.trim();
        if (!id) {
            alert('Por favor, insira o ID.');
            return;
        }
        const contactType = await checkContactTypeExists(id);
        if (!contactType) {
            alert(`Contact type com ID ${id} não encontrado.`);
            return;
        }
        showUpdateFields(contactType);
    }

    //---------------------------------------------------------------------------------
    // Função para atualizar um tipo de contato
    //---------------------------------------------------------------------------------
    async function updateContactType(event) {
        
        event.preventDefault();
        try {
            const formData = new FormData(updateContactTypeForm);
            const data = {};

            formData.forEach((value, key) => {
                data[key] = value.trim() === "" ? null : value.trim();
            });

            console.log('---- Dados a serem enviados ao Backend:', data);

            const response = await fetch(`${backendUrl}/upd-contact-type`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(data)
            });

            console.log('Resposta da solicitação:', response);

            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(`Failed to update contact type: ${response.statusText}, ${errorText}`);
            }
            const result = await response.json();
            alert(result.message || result.error);
        } catch (error) {
            console.error('Error updating contact type:', error);
            alert(`Error updating contact type: ${error.message}`);
        }
    }

    //--------------------------------------------------------------------------------
    // Adiciona eventos aos botões e formulários
    //---------------------------------------------------------------------------------
    listContactTypesButton.addEventListener('click', listContactTypes);
    addContactTypeForm.addEventListener('submit', addContactType);
    deleteContactTypeForm.addEventListener('submit', deleteContactType);
    checkIdForm.addEventListener('submit', checkId);
    updateContactTypeForm.addEventListener('submit', updateContactType);
});
//------------------------------------------------------------------------------------
