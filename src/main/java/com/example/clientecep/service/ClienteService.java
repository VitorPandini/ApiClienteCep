package com.example.clientecep.service;

import com.example.clientecep.entity.Cliente;
import com.example.clientecep.repository.ClienteRepository;
import com.example.clientecep.repository.EnderecoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;

    private final EnderecoRepository enderecoRepository;

    private final ViaCepService viacep;


    @Autowired
    public ClienteService(ClienteRepository clienteRepository, EnderecoRepository enderecoRepository, ViaCepService viacep) {
        this.clienteRepository = clienteRepository;
        this.enderecoRepository = enderecoRepository;
        this.viacep = viacep;
    }


    public Page<Cliente> findAll(Pageable pageable){
        return clienteRepository.findAll(pageable);
    }
    public Page<Cliente> findAllAtivo(Pageable pageable){
        return clienteRepository.findAllByAtivoIsTrue(pageable);
    }

    public Cliente findById(Long id){
        return clienteRepository.findById(id).get();
    }

    public void inserir(Cliente cliente){
        salvarClienteComCep(cliente);
    }

    public void deletar(Long id){
        var clienteId= clienteRepository.findById(id).get();
        clienteId.setAtivo(false);
        clienteRepository.save(clienteId);
    }

    private void salvarClienteComCep(Cliente cliente){
        var cep = cliente.getEndereco().getCep();
        var endereco = enderecoRepository.findById(cep).orElseGet(()->{
            var newEndereco = viacep.consultarCep(cep);
            enderecoRepository.save(newEndereco);
            return newEndereco;
        });
        cliente.setEndereco(endereco);
        clienteRepository.save(cliente);
    }

}
